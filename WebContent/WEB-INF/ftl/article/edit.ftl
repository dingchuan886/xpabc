<#import "../common.ftl" as cc>
<@cc.html_head>
<link rel="stylesheet" href="${bbs_url}js/kindeditor-4.1.10/themes/default/default.css" />
<link rel="stylesheet" href="${bbs_url}js/kindeditor-4.1.10/plugins/code/prettify.css" />
</@cc.html_head>
<@cc.html_index_top>
</@cc.html_index_top>
<#if isGet==1>
<!--contentlogin-->
<div class="wrap super-editor">

    <div id="fast_post" class="rapitReply post_listA">
        <div class="fastReplyBox clearfix">
             <div class="fastReplyBox-form">
                <div class="intTxt">
                    <textarea id="article_content" name="content" style="width:988px;height:600px;visibility:hidden;">${artMap.article_content!}</textarea>
                </div>
                 <div class="ft clearfix">
                    <a href="javascript:void(0);" id="bt_update" class="btn-reply">更新文章</a>
                </div>
            </div>
           <div class="clear"></div>
        </div>
    </div>

</div>
<#else>
	<div id="fast_post" class="rapitReply post_listA">
        <div class="fastReplyBox clearfix">
             <div class="fastReplyBox-form">
                <div style="width:988px;height:600px;">该帖子您无权编辑！</div>
            </div>
           <div class="clear"></div>
        </div>
    </div>
</#if>

<@cc.html_foot>
</@cc.html_foot>
<@cc.html_js>

<#if isGet==1>
<script charset="utf-8" src="${bbs_url}js/kindeditor-4.1.10/kindeditor.js"></script>
<script charset="utf-8" src="${bbs_url}js/kindeditor-4.1.10/lang/zh_CN.js"></script>
<script type="text/javascript">
    var editor;
	KindEditor.ready(function(K) {
		editor = K.create('textarea[name="content"]', {
			allowPreviewEmoticons : false,
			allowImageUpload : true,
			allowFileManager : true,
			allowImageRemote : true,//为true则上传图片时显示“网络图片”功能，为false则上传图片时不显示“网络图片”功能。
		    cssPath : '${bbs_url}js/kindeditor-4.1.10/plugins/code/prettify.css',//指定编辑器iframe document的CSS，用于设置可视化区域的样式。
			uploadJson : '${bbs_url}xpabc/article/imgup',
			resizeType : 0,//2或1或0，2时可以拖动改变文本编辑域的宽度和高度，1时只能改变高度，0时不能拖动。
			items : ['source', '|', 'undo', 'redo', '|', 'code', 'cut', 'copy', 'paste', 'plainpaste', 'wordpaste', '|', 
					'fontname', 'fontsize', 'forecolor', 'hilitecolor', 'bold', 'italic', 'underline',
						'removeformat', '|', 'justifyleft', 'justifycenter', 'justifyright', 'justifyfull', 'insertorderedlist',
						'insertunorderedlist', 'indent', 'outdent', '|', 'emoticons', 'image', 'link', '|', 'fullscreen'],
			afterCreate : function() {
				this.loadPlugin('autoheight');
			}
		});
		
		K("#bt_update").click(function(){
			if(editor.isEmpty()){
				alert("帖子内容不可为空，请输入帖子内容！");
				return;
			}
			$.post("${bbs_url}xpabc/article/update",
					{articleid:${artMap.article_id!},
					content:editor.html()},function(data){
						if(data==-1){
							alert("请首先登录！");
							window.location.reload();
						}
						if(data==0){
							alert("提交失败，请重新提交！");
						}else{
							window.location.href="${bbs_url}xpabc/article/show/" + data + ".htm";
						}
					});
		});
	});
	
</script>
</#if>

</@cc.html_js>
