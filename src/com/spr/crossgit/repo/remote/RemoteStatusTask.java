package com.spr.crossgit.repo.remote;

import com.spr.crossgit.JGitHelper;
import com.spr.crossgit.api.IGitRepository;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javafx.concurrent.Task;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.BranchTrackingStatus;
import org.eclipse.jgit.lib.Ref;

public class RemoteStatusTask extends Task<RemoteStatus> {

    private final IGitRepository repo;

    RemoteStatusTask(IGitRepository r) {
        this.repo = r;
    }

    @Override
    protected RemoteStatus call() throws Exception {
        return getRemoteStatus2();
    }

    private static List<Integer> getCounts(BranchTrackingStatus trackingStatus) throws IOException {
        List<Integer> counts = new ArrayList<>();
        counts.add(trackingStatus.getAheadCount());
        counts.add(trackingStatus.getBehindCount());
        return counts;
}

    private RemoteStatus getRemoteStatus2() throws GitAPIException, IOException {
        final RemoteStatus status = new RemoteStatus();
        for (Ref remote : repo.getRemoteRefs()) {
            System.out.println(remote);
            final Ref local = repo.getRef(remote.getName());
            if (local != null) {
//                final BranchTrackingStatus ts = BranchTrackingStatus.of(repo, remote.getName());
//                if (ts != null) {
//                    status.commitsAhead = ts.getAheadCount();
//                    status.commitsBehind = ts.getBehindCount();
//                    System.out.println(status);
//                    return status;
//                }
            }
        }
        return status;
    }

    private RemoteStatus getRemoteStatus() throws IOException {
        final List<Ref> diffs = new ArrayList<>();
        for (Ref remote : repo.getRemoteRefs()) {
            final Ref local = repo.getRef(remote.getName());
            if (local != null) {
                boolean isRemoteAhead = !JGitHelper.getHash(local)
                        .equals(JGitHelper.getHash(remote));
                if (isRemoteAhead) {
                    diffs.add(remote);
                }
            }
        }
        RemoteStatus status = new RemoteStatus();
        status.setAheadRefs(diffs.stream()
                .map(ref -> ref.getName().replaceAll("refs/heads/", ""))
                .sorted()
                .collect(Collectors.joining(", "))
        );
        return status;
    }
}
