# QQTipsView
仿QQ消息未读拖拽清除，“一键退朝”，“一键清除未读”，“一键下班”

简易效果展示：（包含tab滑动切换变色效果）
![演示界面](https://raw.githubusercontent.com/Qiaoidea/QQTipsView/master/QQTips_demo.gif)

# 如何使用
- ##导入
1.直接导入tipsview至项目作为库/（或直接引入到自己项目）
	（1）Android Studio 在项目 build.gradle 中配置
>  compile project(":tipsview")
	
	（2）eclipse 直接 add library（或在 project.properties 配置）
>  android.library.reference.1=../tipsview


- ##使用
1.将TipsView添加至layout.xml 布局最顶层

``` 
<code.qiao.com.tipsview.TipsView
        android:id="@+id/tip"
        android:layout_width="match_parent"
        android:layout_height="match_parent"/>
```
 或者
```
rootView.addView(tipview, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
```

2.关联至指定可拖动view,并实现拖动响应事件
```
 tipsView.attach(targetView, new TipsView.Listener(){
                @Override
                public void onStart() {
                    targetView.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onComplete() {

                }

                @Override
                public void onCancel() {
                    targetView.setVisibility(View.VISIBLE);
                }
            });
```
**如果是添加在listView的item中，记得在onStart()方法中调用**
```
//当requestDisallowInterceptTouchEvent 参数为true的时候 它不会拦截其子控件的 触摸事件
listView.requestDisallowInterceptTouchEvent(true);
```

- ##方法说明
```
    //缺省方法
    attach(final View attachView, Listener listener)

    attach(final View attachView, final Func<View> copyViewCreator, final Listener listener) 
```
其中，
+ **View attachView** 为点击拖动目标view，比如显示消息未读的view
+ **Func<View> copyViewCreator** 点击拖动时候显示的View,缺省方法默认显示被拖动view本身，当然可以返回其他view，比如选中弹出另外一个view样式。
> 重写invoke()方法返回拖动显示的view

```
         new TipsView.Func<View>() {
                @Override
                public View invoke() {
                    return null;//返回要显示view
                }
            }
```
+  **Listener listener** 点击拖动开始，完成（即消除），取消事件接口
```
new TipsView.Listener(){
                @Override
                public void onStart() {
                          //开始拖动
                }

                @Override
                public void onComplete() {
                         //拖动并移除后
                }

                @Override
                public void onCancel() {
                       //拖动取消
                }
            });
```
实现上述接口便可以达到类似QQ拖动清除效果。