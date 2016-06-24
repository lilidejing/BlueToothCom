文件说明：
AndroidMenifest.xml
res\  资源文件，包括icon，layout,value
src\util\ExitManager.java
src\FrActivity.java    --主页/蓝牙/退出页面控制
src\MyAdapter.java     --文件浏览ListView Adapter
src\HomeActivity.java  --主页 页面
src\ExitActivity.java  --退出 页面
src\BluetoothActivity.java  -- 蓝牙页面
src\BlueListActivity.java  --蓝牙设备ListView
src\BluetoothChatService.java  --蓝牙spp连接服务

从机协议处理在文件BluetoothActivity.java实现，其中实现了蓝牙/设置/浏览器/收音机四个Tab，分别见函数：
JumpToLayoutBlue（）
JumpToLayoutSetting（）
JumpToLayoutPlayer（）
JumpToLayoutFM（）
从机命令发送函数 sendHex（）
数据接收处理函数 handle_recv（） --> handle_1pac()
如果要修改从机协议或者功能，只需要在BluetoothActivity.java文件中修改或增加处理模块，其他java文件一般不需要改动。
