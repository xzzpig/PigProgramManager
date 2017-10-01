# PigProgramManager

## 幻猪的软件管理器

### 子命令帮助

    install:    #安装软件
        参数:
            -d  #安装在指定目录(留空表示当前目录)
            -y  #同意危险操作
    show        #显示软件信息
    search      #搜索软件
    upgrade:    #更新软件
        参数:
            -y  #免除确认(危险操作、主版本号更新)
            -f  #强制更新到最新版本,包括更新号改变
    noupgrade   #禁止/允许软件更新
    update      #更新软件索引
    remove:     #移除软件
    autoremove    #移除为被依赖的软件
    setup       #重新配置软件
    make        #生成软件库列表文件
    gui         #显示为GUI界面
    通用参数:
        -v      #显示更多输出

### 配置(ppmconfig.json)说明

    {
        "programFiles":"${Dir:Programs}", #默认安装路径
        "repertory":[],                   #软件库URL
        "tmpDir":"${Dir:PPM}/Temp/",      #临时文件夹
        "bin":"${Dir:PPM}/bin/",          #Bin路径(希望被用户加入到path中)
        "db":"${Dir:PPM}/data.db"         #数据库文件路径
    }

### 软件安装配置文件

    {
        "name":"",          #软件名称
        "alias":[],         #软件别名
        "homepage":""       #软件官网
        "version":"",       #软件版本
        "versionFormat":"", #版本规则(
                                通过规则来匹配出版本信息(主版本号${0}、次版本号${1}、更新号${2})
                                留空或没有则表示版本规则为 '${0}.${1}.${2}' (此同时为格式化版本)
                                ${0}、${1}、${2}必须为整数
                                更新时，主版本号改变需要确认或加参数'-y',更新号改变默认不升级除非加参数 -f
                            )
        "license":{         #软件协议(可空)
            "name":"",      #软件协议名称
            "url":""        #协议具体内容网址
        },
        "introduce":"",     #软件介绍
        "size":{            #软件大小(可空)
            "download:"",   #下载大小
            "install":""    #安装大小
        },
        "os":""             #os正则(自动转化为小写)
        "depends":[],       #软件依赖(名称:格式化版本)
        "plugins":[],       #依赖插件(未安装则会自动安装)
        "ignoreErrors":{    #执行流程时发生错误是否失败
            "":true         #"ErrorName":boolean
        },
        "steps":{           #流程描述
            "install":[],   #安装: 用于下载，解压，复制等操作并一定要部署好软件实体
            "setup":[],     #配置: 用于创建快捷方式，链接文件等操作
            "uninstall":[], #卸载: 删除软件实体
            "clean":[],     #清理: 删除快捷方式，连接文件，删除配置文件等操作
            "reinstall":[]  #重装: 重装流程的收尾工作
        }
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
    Version:
        $Name:      #某项目的最新版本 eg. ${Version:QQ,0}
        0-2:        #返回的是主版本号(0),次版本号(1),更新号(2)
    Var:
        $Name:      #获取参数的内容 eg. 对于参数 '-d ./' 获取到的是文本 './',对于参数 '-v' 获取到的是文本'true'

### 流程描述

    [
    {
        "name":"Download",  #下载文件
        "url":"",           #下载路径
        "save":""           #保存路径
    },
    {
        "name":"Unzip",     #解压文件
        "file":"",          #文件路径
        "to":"",            #目标路径
        "noname":true       #是否不包含压缩包文件名
    },
    {
        "name":"Move",      #移动文件
        "from":"",
        "to":""
    },
    {
        "name":"Delete",    #删除文件
        "file":""
    },
    {
        "name":"CMD",       #执行命令
        "cmd":"",
        "out":true,         #是否显示标准输出
        "err":false         #是否显示标准错误输出
    },
    {
        "name":"Comfirm",   #需要确认
        "msgs":[""],
        "failErr":"",
        "failMsg":""
    },
    {
        "name":"RegCheck",  #正则校验
        "str":"",
        "reg":"",
        "failErr":"",
        "failMsg":""
    },
    {
        "name":"AddPath",   #添加path到用户环境变量Path中(Windows Only)
        "path":"",
        "ignoreFailed":true
    },
    {
        "name":"Link",
        "from":"",
        "to":"",
        "ignoreFailed":true
    },
    {
        "name":"Script",
        "type":"",
        "url":""
    },
    {
        "name":"Registry",
        "action:"",         #添加/设置:SET|删除:DELETE
        "dir":"",
        "key":"",
        "type":"",
        "value":"",
        "ignoreFailed":true
    },
    {
        "name":"ConfirmDo", #确认执行
        "actions":[],       #同意后执行的流程
        "msg":"",
        "careFail":false    #是否关注执行失败
    }
    ]