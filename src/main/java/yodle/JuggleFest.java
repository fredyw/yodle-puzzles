package yodle;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class JuggleFest {
    private static class Circuit {
        public final String id;
        public final int h;
        public final int e;
        public final int p;
        
        public Circuit(String id, int h, int e, int p) {
            this.id = id;
            this.h = h;
            this.e = e;
            this.p = p;
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append("Circuit [id=");
            builder.append(id);
            builder.append(", h=");
            builder.append(h);
            builder.append(", e=");
            builder.append(e);
            builder.append(", p=");
            builder.append(p);
            builder.append("]");
            return builder.toString();
        }
    }
    
    private static class Juggler {
        public final String id;
        public final int h;
        public final int e;
        public final int p;
        public final List<String> prefs;
        public final Map<String, Integer> dotProducts = new HashMap<>();
        
        public Juggler(String id, int h, int e, int p, List<String> prefs) {
            this.id = id;
            this.h = h;
            this.e = e;
            this.p = p;
            this.prefs = prefs;
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append("Juggler [id=");
            builder.append(id);
            builder.append(", h=");
            builder.append(h);
            builder.append(", e=");
            builder.append(e);
            builder.append(", p=");
            builder.append(p);
            builder.append(", prefs=");
            builder.append(prefs);
            builder.append(", dotProducts=");
            builder.append(dotProducts);
            builder.append("]");
            return builder.toString();
        }
    }
    
    public static void juggleFest(Path path) throws IOException {
        Map<String, Circuit> circuits = new HashMap<>(); // for faster lookup
        LinkedList<Juggler> jugglers = new LinkedList<>();
        Files.readAllLines(path)
            .forEach(line -> {
                if (line.startsWith("C")) {
                    String[] splitLine = line.split("\\s+");
                    circuits.put(
                        splitLine[1],
                        new Circuit(
                            splitLine[1],
                            Integer.parseInt(splitLine[2].split(":")[1]),
                            Integer.parseInt(splitLine[3].split(":")[1]),
                            Integer.parseInt(splitLine[4].split(":")[1])));
                } else if (line.startsWith("J")) {
                    String[] splitLine = line.split("\\s+");
                    Juggler juggler = new Juggler(
                        splitLine[1],
                        Integer.parseInt(splitLine[2].split(":")[1]),
                        Integer.parseInt(splitLine[3].split(":")[1]),
                        Integer.parseInt(splitLine[4].split(":")[1]),
                        Arrays.asList(splitLine[5].split(",")));
                    jugglers.add(juggler);
                }
            });
        // compute the dot product
        jugglers.forEach(juggler -> {
            juggler.prefs.forEach(circuitId -> {
                Circuit circuit = circuits.get(circuitId);
                int value = dotProduct(
                    juggler.h,
                    circuit.h,
                    juggler.e,
                    circuit.e,
                    juggler.p,
                    circuit.p);
                juggler.dotProducts.put(circuitId, value);
            });
        });
        
        int maxSize = jugglers.size() / circuits.size();
        Map<String, Set<Juggler>> result = new HashMap<>();
        // init the result
        circuits.forEach((circuitId, circuit) -> {
            result.put(circuitId, new HashSet<>());
        });
        while (!jugglers.isEmpty()) {
            Juggler juggler = jugglers.peek();
            outer:
            for (String circuitId : juggler.prefs) {
                // is there still a room for this circuit?
                if (result.get(circuitId).size() != maxSize) {
                    result.get(circuitId).add(juggler);
                    jugglers.remove();
                    break;
                } else { // is there anyone better?
                    Iterator<Juggler> iter = result.get(circuitId).iterator();
                    while (iter.hasNext()) {
                        Juggler candidate = iter.next();
                        if (candidate.dotProducts.get(circuitId) < juggler.dotProducts.get(circuitId)) {
                            iter.remove(); // remove it from the result
                            result.get(circuitId).add(juggler);
                            jugglers.remove();
                            // put back the removed juggler
                            jugglers.add(candidate);
                            break outer;
                        }
                    }
                }
            }
        }
        
        result.forEach((circuit, jugglerSet) -> {
            System.out.print(circuit + " ");
            System.out.println(
                jugglerSet.stream()
                .sorted((j1, j2) -> {
                    return j2.dotProducts.get(circuit).compareTo(j1.dotProducts.get(circuit));
                })
                .map(j -> {
                    return j.id + " " + j.dotProducts.entrySet().stream()
                        .map(e -> e.getKey() + ":" + e.getValue())
                        .collect(Collectors.joining(" "));
                })
                .collect(Collectors.joining(", ")));
        });
    }
    
    private static int dotProduct(int h1, int h2, int e1, int e2, int p1, int p2) {
        return (h1 * h2) + (e1 * e2) + (p1 * p2);
    }
    
    public static void main(String[] args) throws IOException {
        juggleFest(Paths.get("testcases/jugglefest/sample.txt"));
        juggleFest(Paths.get("testcases/jugglefest/jugglefest.txt"));
    }
}
