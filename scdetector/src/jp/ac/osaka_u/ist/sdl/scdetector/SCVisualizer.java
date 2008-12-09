package jp.ac.osaka_u.ist.sdl.scdetector;


import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Set;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;


public class SCVisualizer {

    public static void main(String[] args) {

        try {

            //　コマンドライン引数を処理
            final Options options = new Options();

            final Option i = new Option("i", "input", true, "input file");
            i.setArgName("language");
            i.setArgs(1);
            i.setRequired(true);
            options.addOption(i);

            final CommandLineParser parser = new PosixParser();
            final CommandLine cmd = parser.parse(options, args);

            final ObjectInputStream ois = new ObjectInputStream(new FileInputStream(cmd
                    .getOptionValue("i")));
            final Set<ClonePairInfo> clonePairs = (Set<ClonePairInfo>) ois.readObject();

        } catch (ParseException e) {
            e.printStackTrace();
            System.exit(0);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(0);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            System.exit(0);
        }
    }
}
