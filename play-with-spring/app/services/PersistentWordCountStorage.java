package services;

import org.springframework.context.annotation.Scope;

import javax.inject.Named;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 */
@Named("persistentWordCountStorage")
@Scope("singleton")
public class PersistentWordCountStorage {

    private Map<String, Integer> finalReducedMap;

    public PersistentWordCountStorage() {
        finalReducedMap = new ConcurrentHashMap<>();
    }

    public void addWordCount(String word, Integer count) {
        Integer existingCount = finalReducedMap.get(word);
        if (existingCount == null) {
            existingCount = Integer.valueOf(0);
        }

        Integer updatedCount = existingCount + count;
        this.finalReducedMap.put(word, updatedCount);
    }

    public Map<String, Integer> getWordCount() {
        return new HashMap<>(finalReducedMap);
    }
}
