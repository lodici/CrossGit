package com.spr.crossgit.screen.commit;

import com.spr.crossgit.IScreen;
import com.spr.crossgit.api.IGitRepository;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.geometry.Orientation;
import javafx.scene.Parent;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.IndexDiff.StageState;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.AbstractTreeIterator;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.eclipse.jgit.treewalk.FileTreeIterator;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.treewalk.filter.PathFilter;

public class CommitScreen implements IScreen {

    private final BorderPane root = new BorderPane();

    private final ChangeSetPane csPane;
    private final DiffPane diffPane;
    private final IGitRepository<Repository> repo;

    public CommitScreen(IGitRepository aRepo) {

        this.repo = aRepo;

//        debugStatus();
//        debugDiff();

        this.diffPane = new DiffPane(repo);
        this.csPane = new ChangeSetPane(repo.get(), this);
        this.csPane.setUnCommittedFiles(repo.get());

//        // repo bar.
//        HBox.setHgrow(localRepoPane, Priority.NEVER);
//        HBox.setHgrow(remoteRepoPane, Priority.ALWAYS);
//        HBox toolbar = new HBox(localRepoPane, remoteRepoPane);
//        toolbar.setAlignment(Pos.CENTER_LEFT);
//
//        // tabpane
//        VBox.setVgrow(branchesPane, Priority.ALWAYS);
//        VBox.setVgrow(tagsPane, Priority.ALWAYS);
//        final TabPane sidebar = new TabPane();
//        branchesTab.setClosable(false);
//        final VBox branchesBox = new VBox(branchesPane);
//        branchesTab.setContent(branchesBox);
//        sidebar.getTabs().add(branchesTab);
//        tagsTab.setClosable(false);
//        final VBox tagsBox = new VBox(tagsPane);
//        tagsTab.setContent(tagsBox);
//        sidebar.getTabs().add(tagsTab);
//
        // main content SplitPane
        final StackPane sp1 = new StackPane(csPane);
        final StackPane sp2 = new StackPane(diffPane);
        SplitPane sp = new SplitPane();
        sp.getItems().addAll(sp1, sp2);
        sp.setDividerPositions(0.5f);
        sp.setOrientation(Orientation.VERTICAL);

        // Layout
//        root.setTop(toolbar);
//        root.setLeft(sidebar);
        root.setCenter(sp);

    }

    private static AbstractTreeIterator prepareTreeParser(Repository repo, String objectId) throws IOException {
        // from the commit we can build the tree which allows us to construct the TreeParser
        //noinspection Duplicates
        try (RevWalk walk = new RevWalk(repo)) {
            RevCommit commit = walk.parseCommit(repo.resolve(objectId));
            RevTree tree = walk.parseTree(commit.getTree().getId());
            CanonicalTreeParser oldTreeParser = new CanonicalTreeParser();
            try (ObjectReader oldReader = repo.newObjectReader()) {
                oldTreeParser.reset(oldReader, tree.getId());
            }
            walk.dispose();
            return oldTreeParser;
        }
    }

    private void debugDiffEntry(DiffEntry diff) {
        try (DiffFormatter formatter = new DiffFormatter(System.out)) {
            formatter.setRepository(repo.get());
            formatter.setPathFilter(PathFilter.create(diff.getOldPath()));
            AbstractTreeIterator workTreeItr = new FileTreeIterator(repo.get());
            AbstractTreeIterator commitTreeItr = prepareTreeParser(repo.get(), Constants.HEAD);
            List<DiffEntry> diffEntries = formatter.scan(commitTreeItr, workTreeItr);
            for (final DiffEntry entry : diffEntries) {
                System.out.println( "Entry: " + entry + ", from: " + entry.getOldId() + ", to: " + entry.getNewId() );
                formatter.format(entry);
            }
        } catch (IOException ex) {
            Logger.getLogger(CommitScreen.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void debugDiffEntry0(DiffEntry diff) {
        assert diff != null;
        try (DiffFormatter formatter = new DiffFormatter(System.out)) {
            formatter.setRepository(repo.get());
            //formatter.setPathFilter(PathFilter.create(diff.getOldPath()));
            formatter.format(diff);
        } catch (IOException ex) {
            Logger.getLogger(CommitScreen.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void debugDiffEntry1(DiffEntry diff) {
        try (RevWalk walk = new RevWalk(repo.get())) {
            RevCommit revCommit =  walk.parseCommit(repo.get().resolve(Constants.HEAD));
            RevTree tree = revCommit.getTree();
            try (TreeWalk treeWalk = new TreeWalk(repo.get())) {
                treeWalk.addTree(tree);
                treeWalk.setRecursive(true);
                treeWalk.setFilter(PathFilter.create(diff.getOldPath()));
                if (treeWalk.next()) {
                    System.out.println("ITEM=" + treeWalk.getObjectId(0));
                    try (DiffFormatter formatter = new DiffFormatter(System.out)) {
                        formatter.setRepository(repo.get());
//                        formatter.setPathFilter(PathFilter.create(diff.getOldPath()));
                        formatter.format(diff);
                    }
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(CommitScreen.class.getName()).log(Level.SEVERE, null, ex);
        }
    }


    //
    // shows differences between the work directory and the index.
    //
    private void debugDiff() {
        try (final Git git = new Git(repo.get())) {
//            List<DiffEntry> diffs = git.diff().setOutputStream(System.out).call();
            List<DiffEntry> diffs = git.diff().call();
            for (DiffEntry diff : diffs) {
                System.out.println("==============================");
                System.out.println("DIFF: " + diff.getNewPath());
                System.out.println("==============================");
                debugDiffEntry(diff);
                System.out.println("==============================");
            }
        } catch (GitAPIException ex) {
            Logger.getLogger(ChangeSetTable.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void debugStatus() {

        try (Git git = new Git(repo.get())) {
            Status status = git.status().call();
            Set<String> conflicting = status.getConflicting();
            for (String conflict : conflicting) {
                System.out.println("Conflicting: " + conflict);
            }

            Set<String> added = status.getAdded();
            for (String add : added) {
                System.out.println("Added: " + add);
            }

            Set<String> changed = status.getChanged();
            for (String change : changed) {
                System.out.println("Change: " + change);
            }

            Set<String> missing = status.getMissing();
            for (String miss : missing) {
                System.out.println("Missing: " + miss);
            }

            Set<String> modified = status.getModified();
            for (String modify : modified) {
                System.out.println("Modification: " + modify);
            }

            Set<String> removed = status.getRemoved();
            for (String remove : removed) {
                System.out.println("Removed: " + remove);
            }

            Set<String> uncommittedChanges = status.getUncommittedChanges();
            for (String uncommitted : uncommittedChanges) {
                System.out.println("Uncommitted: " + uncommitted);
            }

            Set<String> untracked = status.getUntracked();
            for (String untrack : untracked) {
                System.out.println("Untracked: " + untrack);
            }

            Set<String> untrackedFolders = status.getUntrackedFolders();
            for (String untrack : untrackedFolders) {
                System.out.println("Untracked Folder: " + untrack);
            }

            Map<String, StageState> conflictingStageState = status.getConflictingStageState();
            for (Map.Entry<String, StageState> entry : conflictingStageState.entrySet()) {
                System.out.println("ConflictingState: " + entry);
            }

        } catch (GitAPIException ex) {
            Logger.getLogger(CommitScreen.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public Parent getRoot() {
        return root;
    }

    void showFileDiff(String path) {
        diffPane.showFileDiff(path);
    }

}
