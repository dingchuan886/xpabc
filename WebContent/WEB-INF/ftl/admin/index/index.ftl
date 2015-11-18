<#import "../common.ftl" as cc>
<@cc.html_head>
</@cc.html_head>
<body>
<header class="Hui-header cl">
	<div class="Hui-logo l">小胖爱编程-后台管理</div>
</header>
<aside class="Hui-aside">
	<input runat="server" id="divScrollValue" type="hidden" value="" />
	<div class="menu_dropdown bk_2">
		<dl id="menu-article">
			<dt><i class="Hui-iconfont">&#xe62d;</i> 用户管理<i class="Hui-iconfont menu_dropdown-arrow">&#xe6d5;</i></dt>
			<dd>
				<ul>
					<li><a _href="${bbs_url}xpabc/admin/user/show.htm" href="javascript:void(0)">用户列表</a></li>
				</ul>
			</dd>
		</dl>
		<dl id="menu-article">
			<dt><i class="Hui-iconfont">&#xe61a;</i> 板块管理<i class="Hui-iconfont menu_dropdown-arrow">&#xe6d5;</i></dt>
			<dd>
				<ul>
					<li><a _href="${bbs_url}xpabc/admin/plate/show.htm" href="javascript:void(0)">外部板块</a></li>
					<li><a _href="${bbs_url}xpabc/admin/plate/showin.htm" href="javascript:void(0)">内部板块</a></li>
				</ul>
			</dd>
		</dl>
		<dl id="menu-article">
			<dt><i class="Hui-iconfont">&#xe62d;</i> 版主管理<i class="Hui-iconfont menu_dropdown-arrow">&#xe6d5;</i></dt>
			<dd>
				<ul>
					<li><a _href="${bbs_url}xpabc/admin/plate/master.htm" href="javascript:void(0)">版主列表</a></li>
				</ul>
			</dd>
		</dl>
	</div>
</aside>
<div class="dislpayArrow"><a class="pngfix" href="javascript:void(0);" onClick="displaynavbar(this)"></a></div>
<section class="Hui-article-box">
	<div id="Hui-tabNav" class="Hui-tabNav">
		<div class="Hui-tabNav-wp">
			<ul id="min_title_list" class="acrossTab cl">
				<li class="active"><span title="欢迎使用" data-href="welcome.html">欢迎使用</span><em></em></li>
			</ul>
		</div>
		<div class="Hui-tabNav-more btn-group"><a id="js-tabNav-prev" class="btn radius btn-default size-S" href="javascript:;"><i class="Hui-iconfont">&#xe6d4;</i></a><a id="js-tabNav-next" class="btn radius btn-default size-S" href="javascript:;"><i class="Hui-iconfont">&#xe6d7;</i></a></div>
	</div>
	<div id="iframe_box" class="Hui-article">
		<div class="show_iframe">
			<div style="display:none" class="loading"></div>
			<iframe scrolling="yes" frameborder="0" src="${bbs_url}xpabc/admin/welcome.htm"></iframe>
		</div>
	</div>
</section>
<@cc.html_foot>
</@cc.html_foot>