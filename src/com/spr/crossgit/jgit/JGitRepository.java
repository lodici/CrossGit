package com.spr.crossgit.jgit;

import com.spr.crossgit.GitCommit;
import com.spr.crossgit.api.IGitRepository;
import com.spr.crossgit.branches.BranchesInfo;
import com.spr.crossgit.changeset.ChangeSetFile;
import com.spr.crossgit.repo.remote.RemoteRepoPane;
import com.spr.crossgit.screen.commit.CommitScreen;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ListBranchCommand;
import org.eclipse.jgit.api.ListTagCommand;
import org.eclipse.jgit.api.LogCommand;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.diff.RawTextComparator;
import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.errors.NoWorkTreeException;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.treewalk.AbstractTreeIterator;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.eclipse.jgit.treewalk.FileTreeIterator;
import org.eclipse.jgit.treewalk.filter.PathFilter;
import org.eclipse.jgit.util.io.DisabledOutputStream;

public class JGitRepository implements IGitRepository {

    private final Repository repo;

    public JGitRepository(File f) throws IOException {
        repo = new FileRepositoryBuilder()
            .setGitDir(f)
            .setMustExist(true)
            .build();
    }

    @Override
    public String getBranch() {
        try {
            return repo.getBranch();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public boolean hasUnCommittedChanges() {
        try (Git git = new Git(repo)) {
            return git.status().call().hasUncommittedChanges();
        } catch (GitAPIException | NoWorkTreeException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public String getRemoteUrl() {
        return repo.getConfig().getString("remote", "origin", "url");
    }

    @Override
    public Iterable<Ref> getRemoteRefs() {
        try (Git git = new Git(repo)) {
            return git.lsRemote()
                    .setHeads(true)
                    .call();
        } catch (GitAPIException ex) {
            Logger.getLogger(RemoteRepoPane.class.getName()).log(Level.SEVERE, null, ex);
        }
        return new ArrayList<>();
    }

    @Override
    public Ref getRef(String name) {
        try {
            return name.contains("/") ? repo.exactRef(name) : repo.findRef(name);
        } catch (IOException ex) {
            Logger.getLogger(JGitRepository.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    @Override
    public ObservableList<ChangeSetFile> getChangesetFiles(GitCommit gitCommit) {
        try (RevWalk rw = new RevWalk(repo)) {
            RevCommit revCommit = rw.parseCommit(gitCommit.getId());
            if (revCommit.getParentCount() > 0) {
                RevCommit parent = rw.parseCommit(revCommit.getParent(0).getId());
                DiffFormatter df = new DiffFormatter(DisabledOutputStream.INSTANCE);
                df.setRepository(repo);
                df.setDiffComparator(RawTextComparator.DEFAULT);
                df.setDetectRenames(true);
                List<DiffEntry> diffs = df.scan(parent.getTree(), revCommit.getTree());
                return FXCollections.observableArrayList(diffs.stream()
                        .map(d -> new ChangeSetFile(d))
                        .collect(Collectors.toList()));
            }
        } catch (IncorrectObjectTypeException ex) {
            Logger.getLogger(JGitRepository.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(JGitRepository.class.getName()).log(Level.SEVERE, null, ex);
        }
        return FXCollections.emptyObservableList();
    }

    @Override
    public BranchesInfo getBranches() {
        try (Git git = new Git(repo)) {
            ListBranchCommand cmd = git.branchList();
            List<Ref> branches = cmd.call();
            return new BranchesInfo(branches, repo, git);
        } catch (GitAPIException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public ObservableList<Ref> getTags() {
        try (Git git = new Git(repo)) {
            ListTagCommand cmd = git.tagList();
            List<Ref> tags = cmd.call();
            return FXCollections.observableArrayList(tags);
        } catch (GitAPIException ex) {
            Logger.getLogger(JGitRepository.class.getName()).log(Level.SEVERE, null, ex);
        }
        return FXCollections.emptyObservableList();
    }

    @Override
    public ObservableList<GitCommit> getAllCommits(BranchesInfo branches) {
        try (Git git = new Git(repo)) {
            LogCommand cmd = git.log().all(); // .add(branchInfo.getRefsList().get(7).getObjectId());
            List<GitCommit> gitCommits = new ArrayList<>();
            Iterable<RevCommit> revCommits = cmd.call();
            for (RevCommit revCommit : revCommits) {
                final GitCommit gitCommit = new GitCommit(
                        revCommit, git, branches
                );
                gitCommits.add(gitCommit);
            }
            return FXCollections.observableArrayList(gitCommits);
        } catch (IOException | GitAPIException ex) {
            Logger.getLogger(JGitRepository.class.getName()).log(Level.SEVERE, null, ex);
        }
        return FXCollections.emptyObservableList();
    }

    @Override
    public ObservableList<String> getChangesetFiles() {
        final List<String> files = new ArrayList<>();
        try (Git git = new Git(repo)) {
            Status status = git.status().call();
//            status.getAdded().forEach(f -> files.add(f + " [A]"));
//            status.getChanged().forEach(f -> files.add(f + " [C]"));
//            status.getMissing().forEach(f -> files.add(f + " [X ]"));
//            status.getModified().forEach(f -> files.add(f + " [M]"));
//            status.getRemoved().forEach(f -> files.add(f + " [R]"));
            status.getUncommittedChanges().forEach(f -> files.add(f));
            status.getUntracked().forEach(f -> files.add(f));
        } catch (GitAPIException | NoWorkTreeException ex) {
            Logger.getLogger(JGitRepository.class.getName()).log(Level.SEVERE, null, ex);
        }
        Collections.sort(files);
        return FXCollections.observableList(files);
    }

    private AbstractTreeIterator prepareTreeParser(String objectId) throws IOException {
        // from the commit we can build the tree which allows us to construct the
        // TreeParser noinspection Duplicates
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

    private String getDiffText(ByteArrayOutputStream out, int maxLines) throws UnsupportedEncodingException {
        final String LF = System.lineSeparator();
        final String text = out.toString("UTF-8");
        final String[] textLines = text.split(LF);
        final long totalLines = textLines.length;
        String newText = Stream.of(textLines)
                .limit(maxLines)
                .collect(Collectors.joining(LF));
        if (totalLines > maxLines) {
            newText += LF + LF
                    + "...truncated additional " + (totalLines - maxLines) + " lines.";
        }
        return newText;
    }
    
    @Override
    public String getUnifiedDiff(String filePath) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (DiffFormatter formatter = new DiffFormatter(out)) {
            formatter.setRepository(repo);
            formatter.setPathFilter(PathFilter.create(filePath));
            AbstractTreeIterator workTreeItr = new FileTreeIterator(repo);
            AbstractTreeIterator commitTreeItr = prepareTreeParser(Constants.HEAD);
            List<DiffEntry> diffEntries = formatter.scan(commitTreeItr, workTreeItr);
            assert diffEntries.size() == 1;
            formatter.format(diffEntries.get(0));
            return getDiffText(out, 1000);
        } catch (IOException ex) {
            Logger.getLogger(CommitScreen.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "";
    }
}
