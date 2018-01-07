package nl.cerios.cdbt.app;

import nl.cerios.cdbt.mask.*;
import org.apache.commons.cli.*;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import nl.cerios.cdbt.yaml.StringResolver;
import org.yaml.snakeyaml.representer.Representer;
import org.yaml.snakeyaml.DumperOptions;

import nl.cerios.cdbt.data.TableData;
import nl.cerios.cdbt.read.AbstractReader;
import nl.cerios.cdbt.write.AbstractWriter;

import nl.cerios.cdbt.read.LineReader;
import nl.cerios.cdbt.read.CSVReader;
import nl.cerios.cdbt.write.ImplWriter;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.io.File;
import java.util.LinkedHashMap;

//TODO: Make item limit work
//TODO: Finish Mask implementation
//TODO: Make 2for1 code in Masks neat
//TODO: Advanced Config options
//TODO: Scripting in some simple string functions

/**
 * Created by dwhelan on 30/11/2017.
 */
public class Application {
    //Logging control
    public enum logLevel_ { NONE, FATAL, ERROR, WARN, INFO, DEBUG };
    protected logLevel_ curLogLevel_ = logLevel_.WARN;

    //File handling
    protected String inputDir_;
    protected String inputFile_;
    protected String outputDir_;
    protected String outputFile_;

    //Number of entries
    protected int lineLimit_;

    //Masks
    protected ArrayList<AbstractMask> maskList_;

    //Reader and Writer
    AbstractReader reader_;
    AbstractWriter writer_;

    String _configFile_ = "cfg.yml";

    public Application(){}

    //Runs the application with the given args
    public void run(String[] args){
        Options o = setupOptions();
        parseArgs(args, o);

        outputLog("Parsed args OK", logLevel_.INFO);

        reader_ = new CSVReader();
        writer_ = new ImplWriter();
        maskList_ = new ArrayList<AbstractMask>();

        //If we haven't configured, we can't continue
        if (readConfig() == false) { return; }
        outputLog("Parsed config OK", logLevel_.INFO);
        outputLog("Setup Masks OK", logLevel_.INFO);
        //TODO: List masks as debug logs

        //TODO: How to implement try-with-resources OR finally { close; }?
        try {
            reader_.openFile(compilePath(inputDir_, inputFile_));
            outputLog("Setup Reader OK", logLevel_.INFO);
            outputLog("Data Template: " + reader_.getDataTemplate().toString(), logLevel_.DEBUG);//TODO: Template could be null
            outputLog("Read File: " + inputFile_, logLevel_.DEBUG);

            writer_.openFile(compilePath(outputDir_, outputFile_));
            outputLog("Setup Writer OK", logLevel_.INFO);
            outputLog("Write File: " + outputFile_, logLevel_.DEBUG);

            //==Work
            for (TableData data = reader_.readItem(); data != null; data = reader_.readItem()) {
                for (AbstractMask mask : maskList_) {
                    data = mask.applyMask(data);
                }

                writer_.writeItem(data);
            }

            //Cleanup
            reader_.close();
            writer_.close();
            maskList_.clear();
        }

        catch (IOException e) {
            outputLog(e.getMessage(), logLevel_.FATAL);
            e.printStackTrace();
        }
    }

    //Setup command line options
    private Options setupOptions(){
        Options opt = new Options();

        opt.addOption(Option.builder("h").longOpt("help").desc("Help message").required(false).hasArg(false).build());
        opt.addOption(Option.builder("cfg").longOpt("config").desc("Configuration YAML file").required(false).hasArg(true).build());
        opt.addOption(Option.builder( "v").longOpt("verbosity").desc("Verbosity level (0-5, default 3)").required(false).hasArg(true).optionalArg(true).build());

        return opt;
    }

    //Parse and process options
    private void parseArgs(String[] args, Options options) {
        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = null;
        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        outputLog("===", logLevel_.INFO);
        for (Option s : cmd.getOptions())
        {
            outputLog("arg: " + s.getOpt() + ( s.getValue() != null ? " : " + s.getValue() : ""), logLevel_.INFO);
        }
        outputLog("===", logLevel_.INFO);

        //Help should be the only command executed if present
        if (cmd.hasOption("help")) {
            showHelp();
            return;
        }

        else {
            //Verbosity handling
            if (cmd.hasOption("v")) {
                //Set to passed level
                if (cmd.getOptionValue("v") != null) {
                    curLogLevel_ = logLevel_.values()[Integer.parseInt(cmd.getOptionValue("v"))];
                }

                //Or simply most verbose with no arg
                else curLogLevel_ = logLevel_.DEBUG;

                //Have to disrespect the option to let the user know?
                logLevel_ tempLevel = curLogLevel_;
                curLogLevel_ = logLevel_.INFO;
                outputLog("ARG: Log level set to " + tempLevel + " (" + tempLevel.ordinal() + ")", logLevel_.INFO);
                curLogLevel_ = tempLevel;
            }

            //Config file handling
            if (cmd.hasOption("cfg")) {
                _configFile_ = cmd.getOptionValue("cfg");
               outputLog("ARG: Config file set to " + _configFile_, logLevel_.INFO);
            }
        }
    }

    //Parse config file for entries
    protected boolean readConfig() {
        boolean success = false;

        //Break if the file doesn't exist
        File f = new File(_configFile_);
        if(!f.exists() || f.isDirectory()) {
            outputLog(f.getAbsolutePath() + " not found.", logLevel_.ERROR);
            return success;
        }

        //Do the actual reading
        try (FileInputStream fStream = new FileInputStream(f)) {
            Yaml yaml = new Yaml(new Constructor(), new Representer(), new DumperOptions(), new StringResolver());

            //Process the yml rules
            for (Object o : yaml.loadAll(fStream)) {
                LinkedHashMap<String, LinkedHashMap<String, String>> configEntry = (LinkedHashMap<String, LinkedHashMap<String, String>>)o;
                processConfigEntry(configEntry);
            }

            outputLog(maskList_.size() + " masks initialised.", logLevel_.INFO);
            success = true;
        }

        catch (IOException e) {
            e.printStackTrace();
            outputLog("Error loading .yml file", logLevel_.ERROR);
        }

        return success;
    }

    //Parse an individual config entry
    protected void processConfigEntry(LinkedHashMap<String, LinkedHashMap<String, String>> entry) {

        //Malformed entry
        if (entry.keySet().size() > 1) {
            outputLog("Malformed YAML: Multiple config entries. Keys:", logLevel_.ERROR);
            for (String s : entry.keySet()) {
                outputLog("\t" + s, logLevel_.ERROR);
            }
            return;
        }

        String entryTitle = entry.keySet().iterator().next();

        //Test for entry types
        if (entryTitle.equalsIgnoreCase("Config")) {
            //Process config
            //d("Config");
            LinkedHashMap<String, String> config = entry.get(entryTitle);

            for (String s : config.keySet()) {
                if (s.equalsIgnoreCase("inputdir")) { inputDir_ = config.get(s); }
                else if (s.equalsIgnoreCase("outputdir")) { outputDir_ = config.get(s); }
                else if (s.equalsIgnoreCase("inputfile")) { inputFile_ = config.get(s); }
                else if (s.equalsIgnoreCase("outputfile")) { outputFile_ = config.get(s); }
                else if (s.equalsIgnoreCase("linenum")) { lineLimit_ = Integer.parseInt(config.get(s)); }
            }
        }

        //TODO: Make this detect and ignore blank entries
        else if (entryTitle.equalsIgnoreCase("ColMask")) {

            LinkedHashMap<String, String> maskcfg = entry.get(entryTitle);

            ColMask mask = new ColMask();

            for (String m : maskcfg.keySet())
            {
                mask.addRule(m, maskcfg.get(m));
            }

            maskList_.add(mask);
        }

        else if (entryTitle.equalsIgnoreCase("DataMask")) {
            LinkedHashMap<String, String> maskcfg = entry.get(entryTitle);

            DataMask mask = new DataMask();

            for (String m : maskcfg.keySet())
            {
                mask.addRule(m, maskcfg.get(m));
            }

            maskList_.add(mask);
        }

        else if (entryTitle.equalsIgnoreCase("TypeMask")){
            LinkedHashMap<String, String> maskcfg = entry.get(entryTitle);

            TypeMask mask = new TypeMask();

            for (String m : maskcfg.keySet())
            {
                mask.addRule(m, maskcfg.get(m));
            }

            maskList_.add(mask);
        }

        else if (entryTitle.equalsIgnoreCase("AliasMask")) {
            LinkedHashMap<String, String> maskcfg = entry.get(entryTitle);

            AliasMask mask = new AliasMask();

            for (String m : maskcfg.keySet())
            {
                mask.addRule(m, maskcfg.get(m));
            }

            maskList_.add(mask);
        }

        else {
            outputLog("Unidentified entry: " + entryTitle, logLevel_.ERROR);
        }
    }

    //Combine path and filename TODO: Implement in a less naive way
    protected String compilePath(String path, String file) {
        //We should account for different flavours of slash and trailing
        d(path+file);
        return path + file;

    }

    //Output a log message with a level
    public void outputLog(String logLine, logLevel_ level) {
        if (level.ordinal() <= curLogLevel_.ordinal() && level.ordinal() > 0)
        {
            System.out.println(level + ": " +logLine);
        }
    }

    //Debug output. TODO: Delete this and all references
    public void d (String s) {
        outputLog("D]" + s, logLevel_.DEBUG);
    }

    //Show help text
    protected void showHelp() {
        System.out.println("TODO: Write a real help message");
    }
}
