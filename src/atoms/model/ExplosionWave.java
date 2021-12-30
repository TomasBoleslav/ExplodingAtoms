package atoms.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

class ExplosionWave implements Iterable<Explosion> {
    public ExplosionWave() {
        explosions = new ArrayList<>();
    }

    public void addExplosion(Explosion explosion) {
        explosions.add(explosion);
    }

    public void apply(Board board) {
    }

    public void undo(Board board) {

    }

    public Explosion get(int index) {
        return explosions.get(index);
    }

    public int size() {
        return explosions.size();
    }

    private final List<Explosion> explosions;

    @Override
    public Iterator<Explosion> iterator() {
        return explosions.iterator();
    }
}
