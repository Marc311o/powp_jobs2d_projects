package edu.kis.powp.jobs2d.command.catalog;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.kis.powp.jobs2d.command.DriverCommand;
import edu.kis.powp.observer.Publisher;

public class CommandCatalog {

    private Map<String, DriverCommand> commands = new LinkedHashMap<>();
    private Map<String, List<String>> commandTags = new LinkedHashMap<>();

    private Publisher changePublisher = new Publisher();

    public void addCommand(String name, DriverCommand command) {
        addCommand(name, command, new ArrayList<>());
    }

    public void addCommand(String name, DriverCommand command, List<String> tags) {
        if (name == null || name.trim().isEmpty()) {
            return;
        }

        if (command == null) {
            return;
        }

        String normalizedName = name.trim();

        commands.put(normalizedName, command.deepCopy());
        commandTags.put(normalizedName, normalizeTags(tags));
        changePublisher.notifyObservers();
    }

    public DriverCommand getCommand(String name) {
        if (name == null) {
            return null;
        }

        DriverCommand command = commands.get(name.trim());

        if (command == null) {
            return null;
        }

        return command.deepCopy();
    }

    public List<String> getCommandNames() {
        return new ArrayList<>(commands.keySet());
    }

    public List<String> getCommandTags(String name) {
        if (name == null) {
            return new ArrayList<>();
        }

        List<String> tags = commandTags.get(name.trim());

        if (tags == null) {
            return new ArrayList<>();
        }

        return new ArrayList<>(tags);
    }

    public List<String> getCommandNamesByTag(String tag) {
        List<String> commandNames = new ArrayList<>();

        if (tag == null || tag.trim().isEmpty()) {
            return commandNames;
        }

        String normalizedTag = tag.trim();

        for (Map.Entry<String, List<String>> entry : commandTags.entrySet()) {
            for (String commandTag : entry.getValue()) {
                if (commandTag.equalsIgnoreCase(normalizedTag)) {
                    commandNames.add(entry.getKey());
                    break;
                }
            }
        }

        return commandNames;
    }

    public List<String> getTags() {
        Set<String> tags = new LinkedHashSet<>();

        for (List<String> commandTagList : commandTags.values()) {
            tags.addAll(commandTagList);
        }

        return new ArrayList<>(tags);
    }

    public boolean containsCommand(String name) {
        if (name == null) {
            return false;
        }

        return commands.containsKey(name.trim());
    }

    public Publisher getChangePublisher() {
        return changePublisher;
    }

    private List<String> normalizeTags(List<String> tags) {
        Set<String> normalizedTags = new LinkedHashSet<>();

        if (tags == null) {
            return new ArrayList<>();
        }

        for (String tag : tags) {
            if (tag != null && !tag.trim().isEmpty()) {
                normalizedTags.add(tag.trim());
            }
        }

        return new ArrayList<>(normalizedTags);
    }
}