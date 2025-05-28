package com.bookmyshow.ds;

import org.springframework.util.StringUtils;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Trie {
    private TrieNode root;

    public Trie() {
        root = new TrieNode();
    }

    public List<String> getWordsStartingWith(String prefix) {
    	List<String> results = new LinkedList<>();
    	TrieNode current = root;
    	
    	for (char ch : prefix.toCharArray()) {
    		if (!current.children.containsKey(ch)) {
    			return results; // Prefix not found
    		}
    		current = current.children.get(ch);
    	}
    	
    	collectWords(current, prefix, results);
    	return results;
    }
    
    public void insert(String word) {
        TrieNode current = root;
        for (char ch : word.toCharArray()) {
            current.children.putIfAbsent(ch, new TrieNode());
            current = current.children.get(ch);
        }
        current.isEndOfWord = true;
    }

    public boolean search(String word) {
        TrieNode current = root;
        for (char ch : word.toCharArray()) {
            if (!current.children.containsKey(ch)) {
                return false;
            }
            current = current.children.get(ch);
        }
        return current.isEndOfWord;
    }

    public boolean startsWith(String prefix) {
        TrieNode current = root;
        for (char ch : prefix.toCharArray()) {
            if (!current.children.containsKey(ch)) {
                return false;
            }
            current = current.children.get(ch);
        }
        return true;
    }

    private void collectWords(TrieNode node, String currentPrefix, List<String> results) {
        if (node.isEndOfWord) {
            results.add(StringUtils.capitalize(currentPrefix));
        }
        for (Map.Entry<Character, TrieNode> entry : node.children.entrySet()) {
            collectWords(entry.getValue(), currentPrefix + entry.getKey(), results);
        }
    }

    public void clear() {
        root = new TrieNode(); // Replace the old root with a new empty root.
    }
}
