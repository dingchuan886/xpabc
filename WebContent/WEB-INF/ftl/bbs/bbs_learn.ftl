<#import "../common.ftl" as cc>
<@cc.html_head>
</@cc.html_head>
<@cc.html_index_top>
</@cc.html_index_top>


 <div class="index-con">
        <div class="con-left">
            
        </div>
        <div class="plates2 con-left">
            <ul class='plateul'>
               <#if articleLists?? && articleLists?size gt 0>
						<#list articleLists as list>
         <li class='plateli'>
                <div class='plateul-box'>
                    <h3 class='bg${list.id!}'><a href="${bbs_url}xpabc/index/tobbsList/${list.plateid!}/1.htm">${list.platename!}</a></h3>
                    <div class="plate-sec">
                        <ul class=''>
                        <#if list.articleList?? && list.articleList?size gt 0>
						<#list list.articleList as list2>
                            <li><a href="${bbs_url}xpabc/article/show/${list2.articleid!}.htm">${list2.article_title}</a></li>
                         </#list>
		                </#if>
                        </ul>
                    </div>
                    <div class="readall">
                        <span>文章数: <i>${list.articlecounts!}</i></span>
                        <span>最近发表人: <a href='${bbs_url}xpabc/index/tobbsUserList/0/${list.userid!}/1.htm' class='col-org'>${list.articlename!}</a></span>                        
                    </div>
                </div>
            </li>
          </#list>
		</#if>
            </ul>
        </div>
        <div class="con-right">
            <!-- <div class="search-box">
                <div class="fl" style="line-height:0">
                    <form target="_blank" method="get" id="searchForm" onsubmit="" onkeydown="if(event.keyCode==13){setFormAction();return false;}" _lpchecked="1">
                        <input autocomplete="off" name="q" id="ksWord" class="inpTxt searchlog" type="text" placeholder="输入关键词搜索">                    
                        <input name="Submit" class="inpBtn" type="submit" value="搜索">                    
                        <input type="hidden" name="key" value="cms"></form>
                </div>
            </div> -->
            <div class="con-right-box">
                <h3 class="index-title  bg2">推荐精华</h3>
                <ul class='con-right-list' style="height:301px">
	               <#if jinghuaLists?? && jinghuaLists?size gt 0>
						<#list jinghuaLists as jinghualist>
                        <li><a href="${bbs_url}xpabc/article/show/${jinghualist.articleid!}.htm">${jinghualist.article_title}</a></li>
                        </#list>
                </#if> 
                </ul>
            </div>

            <div class="con-right-box">
                <h3 class="index-title  bg5">招募职位</h3>
                <ul class='con-right-list'>
                    <li><a href="#">编程语言 IDE 对比</a></li>
                    <li><a href="#">最全的静态网站生成器（开源项目）</a></li>
                    <li><a href="#">编程语言 IDE 对比</a></li>
                    <li><a href="#">编程语言 IDE 对比</a></li>
                    <li><a href="#">编程语言 IDE 对比</a></li>
                </ul>
            </div>

            <div class="con-right-box">
                <h3 class="index-title  bg3">编程达人</h3>
                <ul class='con-right-list'>
                    <li><a href="#">编程语言 IDE 对比</a></li>
                    <li><a href="#">最全的静态网站生成器（开源项目）</a></li>
                    <li><a href="#">编程语言 IDE 对比</a></li>
                    <li><a href="#">编程语言 IDE 对比</a></li>
                    <li><a href="#">编程语言 IDE 对比</a></li>
                </ul>
            </div>

        </div>
    </div>



  <@cc.html_foot>
</@cc.html_foot>
<@cc.html_js>
<script>
    $(document).ready(function(){
        $('h3#letterseq a').on('click',function(){
            $this=$(this).index();
             $(this).addClass('cur').siblings().removeClass('cur');
             $('.pf_blist .lettershow').eq($this).show().siblings().hide();
        })
    })
    function checkUser(){
        	var $userID=eval(document.getElementById('bbs_user')).value,
            $userPWD=eval(document.getElementById('bbs_pwd')).value,
            $userErr=document.getElementById('error_login'),
            $userP=document.getElementById('error_text');
            if($userID==''){
                $userErr.style.display="block";
                 $userP.innerHTML='用户名不能为空';
                 return;
            }else{
                if($userPWD==''){
                	$userErr.style.display="block";
                    $userP.innerHTML='密码不能为空';
                    return;
                }
            }
            $.ajax({
				type: "POST",
				url : '${bbs_url}xpabc/user/login',
    			data :{
    				username:$userID,
    				password:$userPWD
    			},
    			success : function(data){
    				var code = data.code;
    				var msg = data.msg;
    				alert(msg);
    				if(code=="0000"){
    					location.href = "${bbs_url}xpabc/userCenter/mine/1.htm";
    				}
    			}
				
			});	
    	}
    
</script>
</@cc.html_js>