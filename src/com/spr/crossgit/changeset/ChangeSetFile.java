package com.spr.crossgit.changeset;

import java.io.File;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffEntry.ChangeType;

public class ChangeSetFile {

    private final DiffEntry diff;
    private final File file;
    private final ChangeType changeType;

    ChangeSetFile(DiffEntry diff) {
//        System.out.println(MessageFormat.format("({0} {1} {2}",
//                diff.getChangeType().name(),
//                diff.getNewMode().getBits(),
//                diff.getNewPath())
//        );
        this.diff = diff;
        this.changeType = diff.getChangeType();
        this.file = new File(changeType == ChangeType.DELETE
                ? diff.getOldPath() 
                : diff.getNewPath()
        );
    }

    File getFile() {
        return file;
    }

    ChangeType getChangeType() {
        return changeType;
    }

    // this has to be public to work with setCellValueFactory().
    public String getText() {
        return toString();
    }

    @Override
    public String toString() {
        switch (changeType) {
            case DELETE:
                return diff.getOldPath();
            case COPY:
                return diff.getOldPath() + " -> " + diff.getOldPath();
            case RENAME:
                return diff.getOldPath() + " -> " + diff.getNewPath();
            default:
                return file.toString();
        }
    }

    String getStyle() {
        switch (changeType) {
            case ADD: return "-fx-text-fill: lightgreen;";
            case DELETE: return "-fx-text-fill: LightCoral;";
            case COPY: return "-fx-text-fill: ForestGreen;";
            case RENAME: return "-fx-text-fill: Orange;";
            default: return null;
        }
    }
}
