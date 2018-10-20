package me.rkfg.avgen;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

public class Main {

    public static void main(String[] args) {
        try {
            Options options = new Options();
            Option animate = Option.builder("a").desc("Animate output").build();
            options.addOption(animate);
            Option size = Option.builder("s").desc("Avatar size (number, default 15)").hasArg().build();
            options.addOption(size);
            DefaultParser parser = new DefaultParser();
            CommandLine parsed = parser.parse(options, args);
            String[] names = parsed.getArgs();
            if (names.length != 1) {
                new HelpFormatter().printHelp("java -jar avgen.jar [OPTIONS] <NAME>", options);
                System.exit(1);
            }
            new Main().run(names[0], Integer.valueOf(parsed.getOptionValue("s", "15")), parsed.hasOption("a"));
        } catch (Exception e) {
            System.err.println("Error parsing options: " + e.getMessage());
        }
    }

    private void run(String name, Integer size, boolean animate) {
        new AvatarFrame(name, size, animate);
    }

}
