# appUpdate
增量更新
<h3>增量升级原理：</h3>

&nbsp;&nbsp;&nbsp;&nbsp;先上传一个apk作为最初的文件，比如说叫版本1，然后每次发布新版本的时候生成一个版本，叫做版本2。<br>
&nbsp;&nbsp;&nbsp;&nbsp;需要在服务器端做一步操作，让版本2和版本1进行比对，生成一个patch文件，这个patch文件就是你需要的增量升级的东西了。<br>

&nbsp;&nbsp;&nbsp;&nbsp;**有2步操作比较重要**<br>

1. patch文件你要很清楚的记录是哪两个版本之间的比较<br>
2. patch文件的生成需要用到NDK，就是需要写c代码，然后编译生成.so文件，集成到你的项目里<br>

&nbsp;&nbsp;&nbsp;&nbsp;第一点，以后每发布一个新版本，就需要进行比对，不是只比对一次，而是以前有多少个版本，就要拿这个新版本和以前的全部对比一次。因为不知道用户下载的是哪一个版本，那么就需要对以前的每一个版本都进行一次对比，这样用户在进行增量升级的时候，才会知道需要下载哪一个patch文件。<br>

&nbsp;&nbsp;&nbsp;&nbsp;第二点，关键是要配置NDK环境<br>

&nbsp;&nbsp;&nbsp;&nbsp;**同时需要考虑的两点：**<br>

&nbsp;&nbsp;&nbsp;&nbsp;数据库容量要够，每生成一个patch文件，就要记录一条，是哪两个版本之间的。<br>

&nbsp;&nbsp;&nbsp;&nbsp;服务器容量也要够，因为以后会发现patch文件会很多，另外每一个版本的apk文件也要保留着，因为如果是第一次下载的话还是需要的。<br>
<br><br>
### 增量升级优缺点  
  
**优点：**  
增量包减少流量损耗，尤其是非WiFi下  

**缺点：**  
增量升级并非完美无缺的升级方式，至少存在以下两点不足： 
 
1. 增量升级是以两个应用版本之间的差异来生成补丁的，我们无法保证用户每次的及时升级到最新，所以必须对所发布的每一个版本都和最新的版本作差分，以便使所有版本的用户都可以差分升级，这样操作相对于原来的整包升级较为繁琐，不过可以通过自动化的脚本批量生成。  
2. 增量升级成功的前提是，用户手机端必须有能够拷贝出来且与服务器用于差分的版本一致的apk，这样就存在，例如，系统内置的apk无法获取到，无法进行增量升级；对于某些与差分版本一致，但是内容有过修改的(比如破解版apk)，这样也是无法进行增量升级的，为了防止合成补丁错误，最好在补丁合成前对旧版本的apk进行sha1sum校验，保证基础包的一致性。
<br><br>
### 增量升级操作  

#### 操作总的来说分3步：
> * 在服务器端，生成这两个版本的差分包；  
> * 在手机客户端，使用已安装的旧版apk与这个差分包，合成为一个新版apk；  
> * 校验新合成的微博客户端文件是否完整，签名时候和已安装客户端一致，如一致，提示用户安装；

#### 过程分析：  
* **1. 生成差分包**  

&nbsp;&nbsp;&nbsp;&nbsp;生成差分包这一步需要在服务器端来实现，一般来说，每当apk有新版本需要提示用户升级，都需要运营人员在后台管理端上传新apk，上传时就应该由程序生成之前所有旧版本们与最新版的差分包。  
<br>  
例如：  
&nbsp;&nbsp;&nbsp;&nbsp;apk已经发布了3个版，V1.0、V2.0、V3.0，这时候你要在后台发布V4.0，那么，当你在服务器上传最新的V4.0包时，服务器端就应该立即生成以下差分包：

 1. V1.0 ——> V4.0的差分包；
 2. V2.0 ——> V4.0的差分包；
 3. V3.0 ——> V4.0的差分包；  

&nbsp;&nbsp;&nbsp;&nbsp;对比的过程：  有一个文件叫bsdiff，bsdiff是二进制差分工具, 用来生成patch文件的，在程序里调它。这个patch叫做差分包。就用这个bsdiff工具，它可以生成patch。  

<hr>
 > 命令：bsdiff oldfile newfile patchfile    
 > 例如: bsdiff xx_v1.0.apk xx_v2.0.apk xx.patch  
  
<hr> 
&nbsp;&nbsp;&nbsp;&nbsp;大致流程如,制作补丁时调用bsdiff函数，根据两个不同版本的二进制文件，生成补丁文件。 将生成的补丁包 xx.patch放置在升级服务器上，供用户下载升级，对应多版本需要对不同的版本进行差分，对于版本跨度较大的，建议整包升级。  
<br>
* **2.使用旧版apk与差分包，在客户端合成新apk**   

&nbsp;&nbsp;&nbsp;&nbsp;使用旧版apk与差分包，需要在手机客户端合成新apk。  
&nbsp;&nbsp;&nbsp;&nbsp;其对应的bspatch是相应的补丁合成工具，可以将patch和apk合成。系统旧版本的apk可以通过copy系统data/app目录下的apk文件获取。  
&nbsp;&nbsp;&nbsp;&nbsp;在android程序里面也要有一段代码，在下载完patch以后，调用bspatch进行合成apk，和差分时的参数一样，即可合成新的apk。  
<hr>
 > bspatch的命令格式为：    
 > bspatch oldfile newfile patchfile  
  
<hr>
* **3.校验新合成的apk文件**  

&nbsp;&nbsp;&nbsp;&nbsp;新包和成之后，还需要对客户端合成的apk包与最新版本apk包进行MD5或SHA1校验，如果校验码不一致，说明合成过程有问题，新合成的包将不能被安装。  
<br>
<br>

* **注意事项**  
&nbsp;&nbsp;&nbsp;&nbsp;增量更新的前提条件，是在手机客户端能让我们读取到当前应用程序安装后的源apk，如果获取不到源apk，那么就无法进行增量更新了。
&nbsp;&nbsp;&nbsp;&nbsp;另外，如果你的应用程序不是很大，比如只有2、3M，那么完全没有必要使用增量更新，增量更新只适用于apk包比较大的情况，比如手机游戏客户端。  
<br>
<br>
 > 注：  
安装bsdiff  
1. Press Command+F and type Terminal and press enter/return key.
2. Run in Terminal app:
<hr>
**ruby -e "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/master/install)" < /dev/null 2> /dev/null**
<hr>
and press enter/return key. Wait for the command to finish.
3. Run: 
<hr> 
**brew install bsdiff**  
<hr>
Done! You can now use bsdiff.


  
  
