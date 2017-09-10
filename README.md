# PigProgramManager

## 幻猪的软件管理器

### 子命令帮助

    install:    #安装软件
        参数:
            -d  #安装在指定目录(留空表示当前目录)
            -y  #同意危险操作
    show        #显示软件名称
    search      #搜索软件
    upgrade:    #更新软件
        参数:
            -y  #免除确认(危险操作、主版本号更新)
            -f  #强制更新到最新版本,包括更新号改变
    noupgrade   #禁止软件更新
    update      #更新软件索引
    remove:     #移除软件
    automove    #移除为被依赖的软件
    import      #导入软件库
    setup       #重新配置软件
    gui         #显示为GUI界面
    通用参数:
        -v      #显示更多输出

### 配置(ppmconfig.json)说明

    {
        "programFiles":"${Dir:Programs}" #默认安装路径
        "repertory":[]                   #软件库URL
        "tmpDir":"${Dir:PPM}/Temp/"      #临时文件夹
        "bin":"${Dir:PPM}/bin/"          #Bin路径(希望被用户加入到path中)
    }

### 支持变量

`基本格式:${变量名:参数...}`

`eg.'${Version:QQ,0}',不支持嵌套`

    Dir:
        Programs:   #默认安装路径
        of:
            $Name:  #某项目的文件安装路径
        Program:    #项目安装路径(包含项目名称)
        Temps:      #临时文件夹
        Temp:       #项目临时文件夹
        Bin:        #可执行文件链接地址,用于存放可执行文件的链接、快捷方式
        PPM:        #PigProgramManager所在目录
        Home:       #用户的home文件夹
    URL:
        JSON:       #此json的URL
        DOT:        #去除json名称的URL
    Version:
        $Name:      #某项目的最新版本 eg. ${Version:QQ,0}
        0-2:        #返回的是主版本号(0),次版本号(1),更新号(2)
    Var:
        $Name:      #获取参数的内容 eg. 对于参数 '-d ./' 获取到的是文本 './',对于参数 '-v' 获取到的是文本true