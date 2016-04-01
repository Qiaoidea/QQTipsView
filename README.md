# QQTipsView
仿QQ消息未读拖拽清除，“一键退朝”，“一键清除未读”，“一键下班”

简易效果展示：（包含tab滑动切换变色效果）

![演示界面](screenshot/TipsDemo.gif)

# 版本更新
``v1.2`` 
	不用再传入TipsView 作为根布局，直接传Activity 即可；
	已对拖动时刻View显示做处理，无需额外关注拖拽事件；
	默认拖拽拦截可滑动父ViewGroup手势事件，可以直接在 ScrollView/ListView等中使用。


- ##使用（V1.0）
　　开袋即食。直接在要使用的地方调用

``` 
 TipsView.create(activity)
            .attach(view , TipsView.DragListener);
```
 
 so easy..


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

[旧版说明][screenshot/oldme.md]