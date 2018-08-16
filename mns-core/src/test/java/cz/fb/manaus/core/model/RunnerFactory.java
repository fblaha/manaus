package cz.fb.manaus.core.model;

public class RunnerFactory {
    public static Runner create(long selectionId, String name, double handicap, int sortPriority) {
        var runner = new Runner();
        runner.setSelectionId(selectionId);
        runner.setName(name);
        runner.setHandicap(handicap);
        runner.setSortPriority(sortPriority);
        return runner;
    }
}
