package com.xzzpig.pigprogrammanager.api;

import com.github.xzzpig.pigutils.annoiation.Const;
import com.github.xzzpig.pigutils.annoiation.NotNull;
import com.github.xzzpig.pigutils.annoiation.Nullable;
import com.github.xzzpig.pigutils.database.*;
import com.github.xzzpig.pigutils.json.JSONArray;
import com.github.xzzpig.pigutils.json.JSONException;
import com.github.xzzpig.pigutils.json.JSONObject;
import com.github.xzzpig.pigutils.json.JSONTokener;
import com.github.xzzpig.pigutils.plugin.PluginManager;

import java.io.*;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@com.github.xzzpig.pigutils.annoiation.API
public class API {

    @NotNull
    public static final List<String> ignoredException = new ArrayList<>();
    public static final TableConstruct TABLE_CONSTRUCT_ALL = new TableConstruct()
            .addDBField(new DBField()
                    .setName("name")
                    .setType(DBFieldType.Text)
                    .setPrimaryKey(true)
                    .setNotNull(true))
            .addDBField(new DBField()
                    .setName("version_0")
                    .setType(DBFieldType.Int)
                    .setDefaultValue(0))
            .addDBField(new DBField()
                    .setName("version_1")
                    .setType(DBFieldType.Int)
                    .setDefaultValue(0))
            .addDBField(new DBField()
                    .setName("version_2")
                    .setType(DBFieldType.Int)
                    .setDefaultValue(0))
            .addDBField(new DBField()
                    .setName("depends")
                    .setType(DBFieldType.Text))
            .addDBField(new DBField()
                    .setName("url")
                    .setType(DBFieldType.Text)
                    .setNotNull(true));
    public static final TableConstruct TABLE_CONSTRUCT_INSTALLED = new TableConstruct()
            .addDBField(new DBField()
                    .setName("name")
                    .setType(DBFieldType.Text)
                    .setPrimaryKey(true)
                    .setNotNull(true))
            .addDBField(new DBField()
                    .setName("version_0")
                    .setType(DBFieldType.Int)
                    .setDefaultValue(0))
            .addDBField(new DBField()
                    .setName("version_1")
                    .setType(DBFieldType.Int)
                    .setDefaultValue(0))
            .addDBField(new DBField()
                    .setName("version_2")
                    .setType(DBFieldType.Int)
                    .setDefaultValue(0))
            .addDBField(new DBField()
                    .setName("json")
                    .setType(DBFieldType.Text)
                    .setNotNull(true))
            .addDBField(new DBField()
                    .setName("loc")
                    .setType(DBFieldType.Text)
                    .setNotNull(true))
            .addDBField(new DBField()
                    .setName("depended")
                    .setType(DBFieldType.Int)
                    .setDefaultValue(0))
            .addDBField(new DBField()
                    .setName("soft")
                    .setType(DBFieldType.Int)
                    .setDefaultValue(0))
            .addDBField(new DBField()
                    .setName("disableUpgrade")
                    .setType(DBFieldType.Int)
                    .setDefaultValue(0));
    @NotNull
    private static final Map<String, List<VariableProvider>> VARIABLE_PROVIDER_MAP = new HashMap<>();
    @NotNull
    private static final Map<String, CommandExecutor> COMMAND_EXECUTOR_MAP = new HashMap<>();
    @NotNull
    private static final Map<String, List<JsonExecutor>> JSON_EXECUTOR_MAP = new HashMap<>();
    @com.github.xzzpig.pigutils.annoiation.API
    @InCommand
    public static Database database;
    @Const(constReference = true)
    public static boolean verbose;
    @com.github.xzzpig.pigutils.annoiation.API(false)
    private static Command command;
    @NotNull
    private static JSONObject config;

    @com.github.xzzpig.pigutils.annoiation.API
    public static void registerValuableProvider(VariableProvider provider) {
        String name = provider.name();
        if (!VARIABLE_PROVIDER_MAP.containsKey(name))
            VARIABLE_PROVIDER_MAP.put(name, new ArrayList<>());
        VARIABLE_PROVIDER_MAP.get(name).add(provider);
    }

    @InCommand
    public static JSONObject getConfig() {return config;}

    @InCommand
    public static @NotNull File getDBFile() {
        return new File(getConfig().optString("db"));
    }

    @com.github.xzzpig.pigutils.annoiation.API
    public static void registerCommandExecutor(CommandExecutor commandExecutor) {
        COMMAND_EXECUTOR_MAP.put(commandExecutor.info().cmd, commandExecutor);
    }

    @com.github.xzzpig.pigutils.annoiation.API
    public static void registerJsonExecutor(JsonExecutor jsonExecutor) {
        String name = jsonExecutor.name();
        if (!JSON_EXECUTOR_MAP.containsKey(name))
            JSON_EXECUTOR_MAP.put(name, new ArrayList<>());
        List<JsonExecutor> list = JSON_EXECUTOR_MAP.get(name);
        list.add(jsonExecutor);
        list.sort(Comparator.comparingInt(JsonExecutor::getExecuteLevel));
    }

    @com.github.xzzpig.pigutils.annoiation.API
    public static Command getCommand() {
        return command;
    }

    private static void loadPlugin(@NotNull Object obj) {
        API.echo("!加载插件:" + obj);
        PluginManager.DefaultPluginManager.loadPlugin(obj);
    }

    public static void loadConfig() {
        File configFile = new File(System.getenv("USERPROFILE") + "/PigProgramManager/config.json");
        if (!configFile.exists()) {
            API.echo("未找到", configFile.getAbsolutePath(), ",将使用程序所在目录下的config.json");
            configFile = new File(getPPMDir(), "config.json");
            if (!configFile.exists()) {
                API.echo("未找到", configFile.getAbsolutePath(), ".请检查配置文件后再试");
                System.exit(1);
            }
        }
        API.echo("!开始加载配置文件:", configFile.getAbsolutePath());
        try (FileReader fileReader = new FileReader(configFile); Scanner scanner = new Scanner(fileReader)) {
            StringBuffer sb = new StringBuffer();
            while (scanner.hasNextLine())
                sb.append(scanner.nextLine()).append('\n');
            config = new JSONObject(solveVars(sb.toString()).replace('\\', '/'));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            API.echo("文件", configFile.getAbsolutePath(), "读取失败");
            throw new RuntimeException(e);
        } catch (JSONException e) {
            API.echo("结构化JSON失败,请确认", configFile.getAbsolutePath(), "的结构为json");
            throw new RuntimeException(e);
        }
    }

    @com.github.xzzpig.pigutils.annoiation.API
    public static boolean executeCommand(@NotNull Command cmd) {
        Command oldCMD = command;
        command = cmd;
        try {
            CommandExecutor executor = COMMAND_EXECUTOR_MAP.get(cmd.cmd);
            if (executor == null) {
                echo("命令帮助:");
                COMMAND_EXECUTOR_MAP.values().stream().map(CommandExecutor::info).map(CommandInfo::toString).forEach(API::echo);
                echo("未找到该命令", "!:" + cmd);
                return false;
            } else {
                String result = executor.execute(cmd);
                if (result != null) {
                    echo(cmd, "命令执行失败,失败原因:", result);
                    return false;
                }
            }
            return true;
        } finally {
            command = oldCMD;
        }
    }

    /**
     * known Exception Name:[NoSuchJsonExecutor,NoJsonExecutorExecuted]
     *
     * @throws JsonExecuteException Json执行错误
     */
    @com.github.xzzpig.pigutils.annoiation.API
    public static void executeJsonObject(String name, JSONObject jsonObject) throws JsonExecuteException {
        List<JsonExecutor> jsonExecutors = JSON_EXECUTOR_MAP.get(name);
        if (jsonExecutors == null || jsonExecutors.size() == 0)
            throw new JsonExecuteException("NoSuchJsonExecutor", "No JsonExecutor named " + name);
        for (JsonExecutor jsonExecutor : jsonExecutors) {
            if (jsonExecutor.execute(jsonObject))
                return;
        }
        throw new JsonExecuteException("NoJsonExecutorExecuted", "All method execute form JsonExecutor named " + name + " return false");
    }

    @com.github.xzzpig.pigutils.annoiation.API
    public static boolean executeJsonObjects(@NotNull JSONArray jsonArray) {
        int len = jsonArray.length();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.optJSONObject(i);
            String name = jsonObject.optString("name");
            API.echo("开始执行步骤:" + name + "(" + (i + 1) + "/" + len + ")");
            try {
                executeJsonObject(name, jsonObject);
            } catch (JsonExecuteException e) {
                if (ignoredException.contains(e.name)) {
                    echo("Json执行发生了错误", e.name + ":" + e.msg, "(已忽略)");
                } else {
                    echo("Json执行发生了错误", e.name + ":" + e.msg, "(停止执行)");
                    return false;
                }
            }
        }
        return true;
    }

    @NotNull
    public static Command solveArgs(String... args) {
        return new Command(args);
    }

    @NotNull
    @com.github.xzzpig.pigutils.annoiation.API
    public static String solveVars(@NotNull String str) {
        List<Variable> variables = findVars(str);
        for (Variable variable : variables) {
            if (!VARIABLE_PROVIDER_MAP.containsKey(variable.name))
                continue;
            str = str.replace(variable.raw, VARIABLE_PROVIDER_MAP.get(variable.name).stream().map(provider->provider.provide(variable.args)).filter(Objects::nonNull).findAny().orElse(variable.raw));
        }
        return str;
    }

    /**
     * @return null if program is not installed
     * @throws RuntimeException on SQLException
     */
    @com.github.xzzpig.pigutils.annoiation.API
    @InCommand
    public static ResultSet getInstalledProgramResult(String program) throws SQLException {
        Table table_installed = API.database.getTable("installed");
        ResultSet resultSet = table_installed.select().setColums("*").setWhere("name = \"" + program + "\"").select();
        if (!resultSet.next()) return null;
        return resultSet;
    }

    @com.github.xzzpig.pigutils.annoiation.API
    @InCommand
    public static int getDependedNum(String program) throws SQLException {
        ResultSet resultSet = getInstalledProgramResult(program);
        return resultSet.getInt("depended");
    }

    @com.github.xzzpig.pigutils.annoiation.API
    @InCommand
    public static void setDependedNum(String program, int num) throws SQLException {
        API.database.getConnection().createStatement().executeUpdate("UPDATE \"installed\" SET depended = " + num + " WHERE \"name\" = \"" + program + "\"");
    }

    @com.github.xzzpig.pigutils.annoiation.API
    @InCommand
    public static void addDependedNum(String program, int num) throws SQLException {
        setDependedNum(program, getDependedNum(program) + num);
    }

    @NotNull
    public static List<Variable> findVars(@NotNull String str) {
        Pattern pattern = Pattern.compile("\\$\\{([^}]+)}", Pattern.MULTILINE);
        Matcher matcher = pattern.matcher(str);
        List<Variable> variables = new ArrayList<>();
        while (matcher.find()) {
            variables.add(new Variable(matcher.group(1)));
        }
        return variables;
    }

    public static void loadPlugins() {
        API.echo("!开始加载插件");
        File pluginDir = new File("./plugins/");
        if (pluginDir.exists()) {
            Arrays.stream(pluginDir.listFiles()).filter(File::isFile).forEach(API::loadPlugin);
        } else {
            pluginDir.mkdirs();
        }
        API.echo("!插件加载完毕");
    }

    @com.github.xzzpig.pigutils.annoiation.API
    public static void echo(@NotNull Object... msgs) {
        String str;
        for (Object obj : msgs) {
            str = obj + "";
            if (str.startsWith("!"))
                if (verbose)
                    str = str.replaceFirst("!", "");
                else
                    continue;
            System.out.print(str);
            System.out.print(" ");
        }
        System.out.println();
    }

    public static File getPPMDir() {
        return getJarFile().getParentFile();
    }

    public static File getJarFile() {
        String path = API.class.getProtectionDomain().getCodeSource().getLocation().getFile();
        try {
            path = java.net.URLDecoder.decode(path, "UTF-8"); // 转换处理中文及空格
        } catch (java.io.UnsupportedEncodingException e) {
            return null;
        }
        return new File(path);
    }

    @com.github.xzzpig.pigutils.annoiation.API
    public static @NotNull JSONObject digestSetupJson(@NotNull JSONObject jsonObject) {
        JSONObject result = new JSONObject();
        result.put("name", jsonObject.optString("name"));
        result.put("alias", jsonObject.optJSONArray("alias"));
        result.put("version", formatVersion(jsonObject.optString("version"), jsonObject.optString("versionFormat")));
        result.put("depends", jsonObject.optJSONArray("depends"));
        result.put("os", jsonObject.optString("os"));
        return result;
    }

    public static @NotNull String formatVersion(@NotNull String version, @Nullable String format) {
        if (format == null)
            format = "${0}.${1}.${2}";
        StringBuffer sb = new StringBuffer();
        Pattern pattern = Pattern.compile(format
                .replace("${0}", "(\\d+)")
                .replace("${1}", "\\d+")
                .replace("${2}", "\\d+"
                ));
        Matcher matcher = pattern.matcher(version);
        if (matcher.find())
            sb.append(matcher.group(1));
        else
            sb.append("0");
        sb.append('.');
        pattern = Pattern.compile(format
                .replace("${0}", "\\d+")
                .replace("${1}", "(\\d+)")
                .replace("${2}", "\\d+"
                ));
        matcher = pattern.matcher(version);
        if (matcher.find())
            sb.append(matcher.group(1));
        else
            sb.append("0");
        sb.append('.');
        pattern = Pattern.compile(format
                .replace("${0}", "\\d+")
                .replace("${1}", "\\d+")
                .replace("${2}", "(\\d+)"
                ));
        matcher = pattern.matcher(version);
        if (matcher.find())
            sb.append(matcher.group(1));
        else
            sb.append("0");
        return sb.toString();
    }

    @com.github.xzzpig.pigutils.annoiation.API
    public static void verbException(Exception e) {
        if (verbose)
            e.printStackTrace();
    }

    /**
     * 初始化数据库
     */
    public static void initDatabase() {
        echo("!正在加载数据库");
        try (Database database = openDatabase()) {
            database.getAllTableNames();
            if (!database.isTableExists("all_")) {
                echo("!正在新建表'all_'");
                initDatabase_All(database);
            }
            if (!database.isTableExists("installed")) {
                echo("!正在新建表'installed'");
                initDatabase_Installed(database);
            }
        } catch (SQLException e) {
            verbException(e);
        } catch (ClassNotFoundException e) {
            verbException(e);
        }
        echo("!数据库加载完成");
    }

    @com.github.xzzpig.pigutils.annoiation.API(false)
    public static @NotNull Database openDatabase() throws SQLException, ClassNotFoundException {
        Class.forName("org.sqlite.JDBC");
        Connection connection = DriverManager.getConnection(getDBFile().toURI().toString().replaceFirst("file:/", "jdbc:sqlite:"));
        return new Database(connection);
    }

    private static void initDatabase_All(Database db) throws SQLException {
        Table table = db.createTable("all_", TABLE_CONSTRUCT_ALL);
    }

    private static void initDatabase_Installed(Database db) throws SQLException {
        Table table = db.createTable("installed", TABLE_CONSTRUCT_INSTALLED);
    }

    @com.github.xzzpig.pigutils.annoiation.API
    @InCommand
    public static JSONObject downloadProgramJson(String program) throws SQLException, IOException {
        Table table_all = database.getTable("all_");
        ResultSet resultSet = table_all.select().setColums("name", "url").setWhere("name = \"" + program + "\"").select();
        if (resultSet.next()) {
            URL url = new URL(resultSet.getString("url"));
            try (InputStream inputStream = url.openStream()) {
                return new JSONObject(new JSONTokener(inputStream)).put("jsonURL", url.toString());
            }
        } else {
            throw new NullPointerException("no program named " + program + " found in the table all_");
        }
    }

    @com.github.xzzpig.pigutils.annoiation.API
    @InCommand
    public static boolean isInstalled(String program) throws SQLException {
        Table table = database.getTable("installed");
        ResultSet resultSet = table.select().setColums("name").setWhere("name = \"" + program + "\"").select();
        if (resultSet.next())
            return true;
        else
            return false;
    }

    @com.github.xzzpig.pigutils.annoiation.API
    public static boolean needConfirm(@Nullable String... msgs) {
        if (msgs != null) {
            for (String msg : msgs) {
                System.out.println(msg);
            }
        }
        System.out.print("是否同意(y/n):");
        if (command != null && command.hasSign("y")) {
            System.out.println("(自动同意)");
            return true;
        }
        Scanner scanner = new Scanner(System.in);
        return scanner.nextLine().equalsIgnoreCase("y");
    }

    public static boolean deleteDir(File dir) {
        if (dir == null || !dir.exists())
            return true;
        if (dir.isFile())
            return dir.delete();
        File[] files = dir.listFiles();
        if (files != null)
            for (File file : dir.listFiles())
                if (!deleteDir(file))
                    return false;
        return dir.delete();
    }
}
