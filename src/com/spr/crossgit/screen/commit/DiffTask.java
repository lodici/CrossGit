package com.spr.crossgit.screen.commit;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javafx.concurrent.Task;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.AbstractTreeIterator;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.eclipse.jgit.treewalk.FileTreeIterator;
import org.eclipse.jgit.treewalk.filter.PathFilter;

public class DiffTask extends Task<String> {
    
    private final Repository repo;    
    private final String filePath;

    public DiffTask(Repository repo, String filePath) {
        this.repo = repo;
        this.filePath = filePath;
    }
        
    private AbstractTreeIterator prepareTreeParser(String objectId) throws IOException {
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
    protected String call() throws Exception {
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
