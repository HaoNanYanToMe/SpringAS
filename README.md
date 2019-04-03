# 什么是Spring A.S？

> 如您所见，Spring A.S是一款让开发人员更加专注于业务层面开发的Spring Boot框架

### 与普通Spring boot及传统项目框架有何不同？

> 针对您关心的问题，您可以阅读如下条目以获取您想要的答案：
> 1. **前后端分离** ：这是一种主流的技术趋势，本框架采用了目前主流vue+spring boot；
>  2. **融合了[Graphql](http://graphql.cn/)理念** ：前后端操作接口统一，极大程度上优化及简化对CRUD的重复性操作（spring A.S目前已对多级连表及group having分组等复杂操作提供了支持，这些操作都可以在前端代码轻松实现~所需字段随需调配一步到位！），让开发人愿再也不必为了反复的单纯的字段更改而焦头烂额；
>  3. **轻量化权限模块** ：相较于shiro和security，Spring A.S提供了更简单及轻量化的操作，直接对表对接口进行细粒度的权限控制并为您提供一整套很cool的权限管理页面；
 > 4. **数据优化引擎** ：以往需要对数据进行多维度的操作筛选时我们往往会选择撰写对应的SQL语句，而需求复杂而多变从而导致我们在开发时浪费许多不必要的精力及时间，现在您只需要使用框架提供的数据优化引擎，您就可以轻松完成对应的操作；
>  5. **eChars引擎**：对eChars数据进行后端封装，目前仅支持折线柱图及环形图（代码还有待优化，*目前仅为内测版本）；
>  6. **pdm代码生成** ：对于Spring A.S的底层支持，您只需要通过框架提供的pdm生成器即可完成对DAO层及核心层代码生成，一键操作，省时省力；
>  7. **更多** ：持续开发中，尽情期待，您可以在留言区提出您宝贵的意见和建议，我将一一回复您的问题及疑惑。

## Spring A.S源码
[前端Vue-github地址](https://github.com/HaoNanYanToMe/SpringASVue)

[服务端Java-github地址](https://github.com/HaoNanYanToMe/SpringAS) 

## 更多有关Spring A.S的资料
[【Spring A.S】技术选型及框架逻辑](https://blog.csdn.net/qq_27047215/article/details/88972310)

[【Spring A.S】核心组件：SqlEngine使用手册：概要](https://blog.csdn.net/qq_27047215/article/details/88989251)

[【Spring A.S】核心组件：SqlEngine使用手册：查询条件（一）](https://blog.csdn.net/qq_27047215/article/details/88993116)

[【Spring A.S】核心组件：SqlEngine使用手册：查询条件（二）](https://blog.csdn.net/qq_27047215/article/details/88993435)

## 前端示例图

**系统菜单管理**：
![菜单管理](https://img-blog.csdnimg.cn/2019040210550474.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzI3MDQ3MjE1,size_16,color_FFFFFF,t_70)

**菜单管理-权限设定**
![权限设定](https://img-blog.csdnimg.cn/20190402105623226.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzI3MDQ3MjE1,size_16,color_FFFFFF,t_70)

**权限管理**
![权限管理](https://img-blog.csdnimg.cn/20190402105817445.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzI3MDQ3MjE1,size_16,color_FFFFFF,t_70)

**菜单授权**
![菜单授权](https://img-blog.csdnimg.cn/20190402105945133.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzI3MDQ3MjE1,size_16,color_FFFFFF,t_70)

**系统用户管理**
![系统用户管理](https://img-blog.csdnimg.cn/20190402110143633.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzI3MDQ3MjE1,size_16,color_FFFFFF,t_70)

**数据优化（规则）引擎**
![优化引擎](https://img-blog.csdnimg.cn/20190402110313909.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzI3MDQ3MjE1,size_16,color_FFFFFF,t_70)

## 示例代码
**相信您已经迫不及待了吧？接下来我将为您简单的介绍一下基本接口在前端的应用方法**

***简单查询***

```
			//获取Table表单数据
			getTableData() {
				var _this = this;
				_this.loading = true;
				let params = {
				    //告知后台对DEPTINFO_NAME字段执行模糊查询
					'DEPTINFO_NAME#LIKE': this.formItem.serchName == '' ? null : '%' + this.formItem.serchName + '%',
					//告知后台对DEPTINFO_ISDELETE字段执行等式查询（ = ）
					'W!DEPTINFO_ISDELETE#EQ': this.formItem.isDelete == '-1' ? null : this.formItem.isDelete,
				}
				//最终以上代码处理结果为：DEPTINFO_NAME LIKE '%?%' AND DEPTINFO_ISDELETE = 1
				this.$http.selectBase(this,
					"DEPTINFO",
					"DEPTINFO",
					"",
					"DEPTINFO_CREATETIME",
					"4",
					_this.pageNo, _this.pageSize,
					params, 0, res => {
						_this.tableData = res.data.data;
						_this.totalItem = res.data.total;
						_this.loading = false;
					})
			},
```
如上例所示:

```
   /*
     * selectBase方法(基本查询)
     * @param currenPage 当前页,传入页面this对象即可
     * @param type 判断是否需否需要对返回的数据进行处理 0-需要,1-不需要
     * @param response 请求成功时的回调函数
     * @param exception 异常的回调函数
     */
    selectBase(currenPage, selTable(要查询的主表), tables（查询传入表的所有字段）, columns（查询指定查询的字段）, sortColumns（需要执行排序的字段）, sortTypes（排序的类型，正序倒叙等）,
    		pageNo（当前第几页）, pageSize（每页多少条）, data（传入的params JSON对象）, type, response, exception) {}
```
查询结果
![查询结果](https://img-blog.csdnimg.cn/20190402113946630.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzI3MDQ3MjE1,size_16,color_FFFFFF,t_70)
**感觉有些复杂？**
**不用担心，我将在近期完善博客的同时开发一款小工具集成在项目中，使用这款小工具您可以轻松配置并获取指定的以上的代码~让您更加便捷的应用Spring A.S**

——**更多关于Spring A.S信息还请关注我的博客~**

——**项目正在最终的调试，预计4月3日会正式上传至github~**

——**如果您有更好建议或意见可以在留言区给我留言~**
