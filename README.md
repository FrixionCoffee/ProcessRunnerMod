# Process Runner Mod

minecraftのMODですが、かなりニッチなので殆どの人には無用の長物です。
このMODは設定ファイルに記述された内容のスクリプトやコマンドをinit時に実行します。

「クライアント起動時にローカルのサーバを起動させlocalhostでログインできるようにし、クライアントとサーバ側両方のデバッグをしやすくすること」や、
「ワールドデータを個別にGitで管理し変更があればコミットするjarファイルをゲーム起動時に実行する」、「ログファイルが100を超えたら古い順に削除していく」といった用途に使用できます。
実行スクリプトなどはご自身で用意してください。

サーバ側では.batやシェルスクリプトに書き加えるだけで起動前に外部プロセス実行などができるため導入する意味はありません。また、サーバ側にはjnaが入っていないのでクラッシュします。

Windowsでのみ起動します。※後述しますが意図的に管理者権限では動作せず、クラッシュするようにしています。

<s> ~~[English Readme.md](./README.md)~~ </s>

## 導入方法

modsフォルダに導入する

configフォルダにprocess_runner_mod.propertiesが生成される。

生成されたファイルの設定値を実行したい外部プロセスや外部スクリプトなどに書き換える

以上

## 注意事項
**ライセンスにもあるように自己責任で使ってください。当然起動した外部プロセス等の実行結果や過程も含めて自己責任です。**

**インターネットからダウンロードしたよく分からない.jarや.exe、.bat等は実行しないでください**
<hr>

管理者権限でMODやマインクラフトランチャー、JREを起動しないでください。

管理者権限でマインクラフトランチャーが起動されるとランチャー-> JRE -> Minecraft -> Forge -> MODといったように管理者権限が伝播しMODまで管理者権限で起動されてしまいます。

MODが管理者権限で動作すると、このMODが起動する外部プロセス等も管理者権限で動作してしまうため、
このMODでは実行前にjnaライブラリを使って「WindowsAPIであるIsUserAnAdmin」を実行してtrueが帰ってきたらエラーをスローし、外部プロセス等を実行しないようにしています。(実装はAdminManager.javaとShell32Dll.java参照)

管理者権限での起動を防止する理由としては、ゲームのMODが管理者権限が必要な外部プロセス等を起動すべきではないこと。誤ったスクリプト等によってOSに影響がある予期しない動作防止などです。

どうしても管理者権限で動作させたい場合はご自身でソースコードを書き換えてください。

## process_runner_mod.propertiesについて

初回起動時には以下のようにファイルが生成されます
\#が先頭についている行はコメントとして解釈されます。
また、:や\、=など一部の記号はエスケープが必要なので"\"をつけてください。
```
writeExitCodeMode=true
temporaryAllIgnoreMode=false
logExportFilePath=ゲームフォルダ\\ProcessRunnerLast.log
printLog4jInfoMode=true
commandArgument=/c example.bat
commandFilePath=C\:\\Windows\\system32\\cmd.exe
processWaitForMode=true
processBuilderDirPath=ゲームフォルダのパス
```

### 各種設定について

```
writeExitCodeMode=true
```
外部コマンド・スクリプト・プロセスから渡される終了コードをログの最後に付与するかどうかを決定します
<hr>

```
temporaryAllIgnoreMode=false
```
trueのときは外部プロセスを実行しません。

modsにこのMODを入れるが一時的に実行してほしくないときに使用します。なお、trueであっても非管理者権限チェックは実行されます。
(実装的には実行しないというよりも何もしないExecuteクラスが生成され実行されます)
<hr>

```
logExportFilePath=ゲームフォルダ\\ProcessRunnerLast.log
```
ログファイルの出力先

後述しますがxxxPathというように、最後にPathが付く設定はPaths.get(設定値)が解釈するため、相対パスやlinux表記でも動作します。
<hr>

```
printLog4jInfoMode=true
```
外部プロセス等の実行開始や終了、ストリームのクローズ完了といった情報をマイクラのログに書き込むかどうかを決定します。
<hr>

```
commandArgument=/c example.bat
```
外部プロセス等に渡す引数を指定します。半角スペースで1つの区切りとなります。
例示の/cはコマンドプロンプトが.bat実行完了後、自身を即終了させるためのオプションです。

ProcessBuilderはコマンドプロンプトの画面は出さないため、/cが無いとコマンドプロンプトが裏でずっと待機状態になってしまいます。

実装としてはProcessBuilderに、{commandFilePath, commandArgumentをスペース区切りに分割}といったList<String>として渡されます。
渡す前にList中のnullや空白要素はstream.filter()によって除去されます。
<hr>

```
commandFilePath=C\:\\Windows\\system32\\cmd.exe
```
外部プロセス等のパスを指定します。

なおProcessBuilderはcmdといった単純な文字列でも実行は可能ですが、WindowsのシステムPathを見に行くとは限らず、cmdを何と解釈するのかはProcessBuilderが決めるようなので、曖昧な解釈ができないようにできるだけファイルパスで記述してください
<hr>

```
processWaitForMode=true
```

外部プロセス等を実行時に外部プロセス等の終了をMODが待機するかどうかを決定します。

原則としてtrueで問題ありませんが、プロセスが終了せず実行させ続けるケースではfalseにする必要があります。

例えば単にマイクラのサーバを起動する.batだと、サーバが起動する≠プロセスの終了であり、stopコマンドの実行完了や異常終了=プロセスの終了なので、
trueだとサーバが止まるまでクライアント側のゲームがフリーズすることになります。

falseの場合、ExecuteFactoryのThreadProcessObserverクラスが別スレッドで外部プロセス等の状況監視を行うためマイクラはフリーズしません。
オブザーバクラスが外部プロセスの終了を検知すると、ストリームのクローズ処理が行われます。

<hr>

```
processBuilderDirPath=ゲームフォルダのパス
```
外部プロセスのカレントディレクトリを設定します。

ここで設定した後でもスクリプト中でcd /d Path...というようにカレントディレクトリの変更をした方が確実です。
空白の場合Paths.get("")によりゲームフォルダと解釈されます。

### 設定名の最後に...Pathが付く設定について
これらの設定はjava.nio.Pathsのgetメソッドに設定値が渡されるため、柔軟な設定が可能です。
ゲームフォルダがCドライブ直下のgameフォルダだった時

```
processBuilderDirPath=C\:\\game\\    # エスケープがあるので、Paths.get("C:\game\")
processBuilderDirPath=./            #相対パス   Paths.get("./")
processBuilderDirPath=              # Paths.get("")
processBuilderDirPath=C:/game/      #Linux等で使用される/区切りの表記
```
これらは同一なファイルパスとして扱われます。(多分)

commandFilePathを除き、ゲームフォルダの絶対パス(+α)なので、必要がなければそのままで問題ありません。
## 動作確認済バージョン

working now.(作業中)

### Forge(Java8)