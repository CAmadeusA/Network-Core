package com.camadeusa.utility;

import java.util.List;

import gnu.trove.map.TObjectDoubleMap;
import gnu.trove.map.hash.TObjectDoubleHashMap;

public interface Eloable {
    public static final int DEFAULT_INITIAL = 1000;
    public static final int DEFAULT_K = 32;

    public int getElo();

    public void deltaElo(int var1);

    default public int getInitialElo() {
        return 1000;
    }

    default public int getKFactor() {
        return 32;
    }

    public static final class Calculations {
        private Calculations() {
        }

        public static final void applyRound(List<Eloable> outcome) {
            Calculations.calcRound(outcome).forEachEntry((eloable, delta) -> {
                eloable.deltaElo((int)Math.floor(delta));
                return false;
            }
            );
        }

        public static final <E extends Eloable> TObjectDoubleMap<E> calcRound(List<? extends E> outcome) {
            TObjectDoubleHashMap deltas = new TObjectDoubleHashMap();
            for (Eloable eloable : outcome) {
                deltas.put((Object)eloable, Calculations.calcPositional(eloable, outcome));
            }
            return deltas;
        }

        public static final void applySingle(Eloable subject, Eloable against, Outcome outcome) {
            subject.deltaElo((int)Math.floor(Calculations.calcDelta(subject, against, outcome)));
        }

        public static final double calcPositional(Eloable subject, List<? extends Eloable> all) {
            double opponents = (double)all.size() - 1.0;
            double actual = opponents - (double)all.indexOf(subject);
            double expected = all.stream().filter(eloable -> !subject.equals(eloable)).mapToDouble(other -> Calculations.calcExpected(subject, other)).sum();
            double delta = (double)subject.getKFactor() * ((actual - expected) / opponents);
            double elo = subject.getElo();
            return elo < (double)subject.getInitialElo() && delta < 0.0 ? delta * (elo / (double)subject.getInitialElo()) : delta;
        }

        public static final double calcDelta(Eloable subject, Eloable against, Outcome outcome) {
            double expected = Calculations.calcExpected(subject, against);
            double delta = (double)subject.getKFactor() * (outcome.value - expected);
            double elo = subject.getElo();
            return elo < (double)subject.getInitialElo() && delta < 0.0 ? delta * (elo / (double)subject.getInitialElo()) : delta;
        }

        public static final double calcExpected(Eloable subject, Eloable against) {
            return 1.0 / (1.0 + Math.pow(10.0, (double)(against.getElo() - subject.getElo()) / 400.0));
        }
    }

    public static enum Outcome {
        WON(1.0),
        DRAWN(0.5),
        LOST(0.0);
        
        private final double value;

        private Outcome(double value) {
            this.value = value;
        }
    }

}