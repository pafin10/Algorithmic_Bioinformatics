package ahoCorasick;

import java.util.*;

class TrieNode {
    Map<Character, TrieNode> children = new HashMap<>();
    boolean isLeaf = false;
    TrieNode failureLink;
    String pattern = "";
    int level = 0;
    public void setFailureLink(TrieNode failureLink) {
        this.failureLink = failureLink;
    }
}
public class Trie {
    private final int [] out = new int [10 * 1000];
    TrieNode root = new TrieNode();
    public Trie() {
        root.setFailureLink(root);
    }

    public void insert(String word) {
        TrieNode current = root;
        int level = 0;
        for (char c : word.toCharArray()) {
            // see if there are any children of current node via c - edge, else add node
            current = current.children.computeIfAbsent(c, k -> new TrieNode());
            current.pattern = word.substring(0, level + 1);
            current.level = ++level;
        }
        current.isLeaf = true;
    }

    public void setFailureLinks() {
        // Using a queue is more compatible with level by level processing than nested loops.
        // We want something more akin to BFS than DFS.

        Queue<TrieNode> queue = new LinkedList<>();

        // Initialize the first level of failure links for the root's children
        for (Map.Entry<Character, TrieNode> entry : root.children.entrySet()) {
            TrieNode childNode = entry.getValue();
            childNode.setFailureLink(root);
            queue.add(childNode);
        }

        // BFS to set failure links for all nodes
        while (!queue.isEmpty()) {
            TrieNode currentNode = queue.poll();

            // Set failure links for each child of currentNode
            for (Map.Entry<Character, TrieNode> entry : currentNode.children.entrySet()) {
                char edgeChar = entry.getKey();
                TrieNode childNode = entry.getValue();

                TrieNode failure = currentNode.failureLink;
                if (failure.children.containsKey(edgeChar)) {
                    failure = failure.children.get(edgeChar);
                }

                childNode.setFailureLink(failure);
                queue.add(childNode);
            }
        }
    }

}
