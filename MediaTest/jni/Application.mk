#1.指定application里要链接的标准c++库
APP_STL := gnustl_static
#2.编译选项
APP_CPPFLAGS := -frtti -DCC_ENABLE_CHIPMUNK_INTEGRATION=1
APP_ABI := armeabi armeabi-v7a