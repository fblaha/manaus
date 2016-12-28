package cz.fb.manaus.core.model;


public interface SideMixed<T extends SideMixed> {

    T getHomogeneous(Side side);

}
