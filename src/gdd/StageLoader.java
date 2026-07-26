package gdd;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Reads a stage's spawn table out of a CSV so waves can be retuned without
 * recompiling.
 *
 * Format is one spawn per line: frame,type,x,y - blank lines and lines starting
 * with '#' are ignored. Several spawns may share a frame, which the old
 * HashMap&lt;Integer, SpawnDetails&gt; could not express.
 */
public class StageLoader {

    private StageLoader() {
        // Prevent instantiation
    }

    public static HashMap<Integer, List<SpawnDetails>> load(String path) {
        HashMap<Integer, List<SpawnDetails>> spawnMap = new HashMap<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String line;
            int lineNo = 0;

            while ((line = reader.readLine()) != null) {
                lineNo++;
                line = line.trim();

                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }

                String[] parts = line.split(",");
                if (parts.length < 4) {
                    System.err.println(path + ":" + lineNo + " expected frame,type,x,y");
                    continue;
                }

                try {
                    int frame = Integer.parseInt(parts[0].trim());
                    String type = parts[1].trim();
                    int x = Integer.parseInt(parts[2].trim());
                    int y = Integer.parseInt(parts[3].trim());

                    spawnMap.computeIfAbsent(frame, k -> new ArrayList<>())
                            .add(new SpawnDetails(type, x, y));
                } catch (NumberFormatException e) {
                    System.err.println(path + ":" + lineNo + " bad number: " + line);
                }
            }
        } catch (Exception e) {
            // A missing stage file should not take the game down; the stage just
            // scrolls by empty and the problem is reported once.
            System.err.println("Could not read stage file " + path + ": " + e.getMessage());
        }

        return spawnMap;
    }
}
