package moe.feo.lucaonegui.util;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class FastaUtil {
    public static List<String> splitFasta(File file, int maxPerChunk) {
        List<String> chunks = new ArrayList<>();
        List<String> currentChunk = new ArrayList<>();
        int count = 0;

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                new FileInputStream(file), StandardCharsets.UTF_8))) {
            String line;
            StringBuilder entry = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                if (line.startsWith(">")) {
                    if (entry.length() > 0) {
                        currentChunk.add(entry.toString());
                        entry.setLength(0);
                        count++;
                        if (count >= maxPerChunk) {
                            chunks.add(String.join("\n", currentChunk));
                            currentChunk.clear();
                            count = 0;
                        }
                    }
                }
                entry.append(line).append("\n");
            }
            if (entry.length() > 0) {
                currentChunk.add(entry.toString());
            }
            if (!currentChunk.isEmpty()) {
                chunks.add(String.join("\n", currentChunk));
            }
        } catch (IOException e) {
            LogUtil.log("Failed to split FASTA file: " + e.getMessage());
        }

        return chunks;
    }
}
