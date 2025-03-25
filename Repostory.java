/*
 * Veer Desai
 * 2/9/2025
 * CSE 123
 * P1: Mini-Git
 * TA: Isayiah Lim
 */
import java.util.*;

import java.text.SimpleDateFormat;
 /*
  * This class acts like a mini-git system. It can build a repository and can store many 
  *      commit versions to it. This can also merge two repositories into one big repository.
  */
public class Repository {
    private String name;
    private Commit commit;

    /*
     * Constructs a Repository which takes in a name to give to the repository. 
     * Exceptions: 
     *      - IllegalArgumentException(): gets thrown if the name to be given to the repo 
     *          is either null or empty
     */
    public Repository(String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("invalid name for repo");
        }
        this.name = name;
    }

    /*
     * Retrieves the id of the current head commit of the repository. 
     * Return: 
     *      - String: the id of the current head commit. 
     *          Or is null if the current head commit is null. 
     */
    public String getRepoHead() {
        if (commit == null) {
            return null;
        }
        return commit.id;    
    }

    /*
     * Retrives the number of commits in the repository.
     * Return: 
     *      - int: the number of commits in the repository
     */
    public int getRepoSize() {
        Commit curr = commit;
        int size = 0;
        while (curr != null) {
            size++;
            curr = curr.past;
        }
        return size;
    }

    /*
     * This returns the underlying information about the current head commit.
     * Information includes the repo's name, the head commit's unique identifier,
     * the head commit's timestamp, and the head commit's message. 
     * Return: 
     *      - String: the head commit's information consisting of the information mentioned above.
     *          If the head commit is null, then this would return the fact saying that there
     *              are no commits available
     */
    public String toString() {
        String answer = this.name + " - ";
        if (commit == null) {
            return answer + "No commits";
        }
        answer += "Current head: " + commit.toString();
        return answer;
    }

    /*
     * This dtermines whether any commit in the repository contains the specified target id. 
     * Parameters: 
     *      - targetId: the target id to find in a commit
     * Exceptions: 
     *      - IllegalArgumentException(): gets thrown if the provided targetId is null
     * Return: 
     *      - boolean: true if a commit in the repo contains the targetId or false if it does not
     */
    public boolean contains(String targetId) {
        if (targetId == null) {
            throw new IllegalArgumentException("the id cannot be null");
        }
        Commit curr = commit;
        while (curr != null) {
            if (curr.id.equals(targetId)) {
                return true;
            }
            curr = curr.past;
        }
        return false;
    }

    /*
     * Gives out the history of the n amount of recent commits to the repository 
     *      in readable format.
     * Parameters: 
     *      - n: the amount of recent commits to be shown. If n is greater than the amount of 
                overall commits, then every single commit in the repo will be shown
     * Exceptions: 
     *      - IllegalArgumentException(): gets thrown if the amount of commits to be shown 
     *          is either 0 or a negative number
     * Return: 
     *      - String: the commit history for the repo with only the amount specified included. 
     *          If the head commit is null, then an empty string will be returned instead. 
     * 
     */
    public String getHistory(int n) {
        if (n <= 0) {
            throw new IllegalArgumentException("number must be positive");
        }
        String answer = "";
        Commit curr = commit;
        int counter = 0;
        while (curr != null && counter < n) {
            answer += curr.toString() + "\n";
            curr = curr.past;
            counter++;
        }
        return answer;
    }

    /*
     * Creates a new commit for the repository with the given message. 
     * Parameters: 
     *      - message: the message to be included with the new commit
     * Exceptions: 
     *      - IllegalArgumentException(): gets thrown if the message to be given to the new commit
     *          is null
     * Return: 
     *      - String: the id of the new commit
     */
    public String commit(String message) {
        if (message == null) {
            throw new IllegalArgumentException("the message cannot be null");
        }
        commit = new Commit(message, commit);
        return commit.id;
    }

    /*
     * Removes the commit with the specified target id, but maintains the history of all 
     *      of the other commits. 
     * Parameters: 
     *      - targetId: the id of the commit that is desired to be removed
     * Exceptions: 
     *      - IllegalArgumentException(): gets thrown if the targetId is null 
     * Return: 
     *      - boolean: returns true if a commit drop was successful. Returns false if 
     *          there was no commit with the target id found
     */
    public boolean drop(String targetId) {
        if (targetId == null) {
            throw new IllegalArgumentException("the target id cannot be null");
        }
        if (commit != null) {
            if (commit.id.equals(targetId)) {
                commit = commit.past;
                return true;
            }
            Commit curr = commit;
            while (curr.past != null) {
                if (curr.past.id.equals(targetId)) {
                    curr.past = curr.past.past;
                    return true;
                }
                curr = curr.past;
            }
        }
        return false;
    }

    /*
     * This method synchonrizes two repositories together into one big commit. This still maintains 
     *      chronological order of all commits regardless of what place they stood when the the two
     *      repos were seperate. The other repository that is combined with this repository is 
     *      left empty in the end. 
     * Parameters: 
     *      - other: the repository that is meant to be combined with this repository
     * Exceptions: 
     *      - IllegalArgumentException(): gets thrown if the other repository is null    * 
     */
    public void synchronize(Repository other) {
        if (other == null) {
            throw new IllegalArgumentException("other repo cannot be null!");
        }
        if (this.commit == null) {
            commit = other.commit;
            other.commit = null;
        }
        if (commit != null && other.commit != null) {
            if (commit.timeStamp < other.commit.timeStamp) {
                Commit temp = commit;
                commit = other.commit;
                other.commit = other.commit.past;
                commit.past = temp;           
            }
            Commit curr = this.commit;
            while (curr.past != null && other.commit != null) {
                if (curr.past.timeStamp < other.commit.timeStamp) {
                    Commit storage = other.commit;
                    other.commit = other.commit.past;
                    storage.past = curr.past;
                    curr.past = storage;  
                }
                curr = curr.past;
            }
            if (other.commit != null) {
                curr.past = other.commit;
            }
            other.commit = null;
        }           
    }
    


    /**
     * A class that represents a single commit in the repository.
     * Commits are characterized by an identifier, a commit message,
     * and the time that the commit was made. A commit also stores
     * a reference to the immediately previous commit if it exists.
     *
     * Staff Note: You may notice that the comments in this 
     * class openly mention the fields of the class. This is fine 
     * because the fields of the Commit class are public. In general, 
     * be careful about revealing implementation details!
     */
    public static class Commit {

        private static int currentCommitID;

        /**
         * The time, in milliseconds, at which this commit was created.
         */
        public final long timeStamp;

        /**
         * A unique identifier for this commit.
         */
        public final String id;

        /**
         * A message describing the changes made in this commit.
         */
        public final String message;

        /**
         * A reference to the previous commit, if it exists. Otherwise, null.
         */
        public Commit past;

        /**
         * Constructs a commit object. The unique identifier and timestamp
         * are automatically generated.
         * @param message A message describing the changes made in this commit. Should be non-null.
         * @param past A reference to the commit made immediately before this
         *             commit.
         */
        public Commit(String message, Commit past) {
            this.id = "" + currentCommitID++;
            this.message = message;
            this.timeStamp = System.currentTimeMillis();
            this.past = past;
        }

        /**
         * Constructs a commit object with no previous commit. The unique
         * identifier and timestamp are automatically generated.
         * @param message A message describing the changes made in this commit. Should be non-null.
         */
        public Commit(String message) {
            this(message, null);
        }

        /**
         * Returns a string representation of this commit. The string
         * representation consists of this commit's unique identifier,
         * timestamp, and message, in the following form:
         *      "[identifier] at [timestamp]: [message]"
         * @return The string representation of this collection.
         */
        @Override
        public String toString() {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
            Date date = new Date(timeStamp);

            return id + " at " + formatter.format(date) + ": " + message;
        }

        /**
        * Resets the IDs of the commit nodes such that they reset to 0.
        * Primarily for testing purposes.
        */
        public static void resetIds() {
            Commit.currentCommitID = 0;
        }
    }
}
