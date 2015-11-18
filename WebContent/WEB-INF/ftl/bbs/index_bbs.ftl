<#import "../common.ftl" as cc>
<@cc.html_head>
</@cc.html_head>
<@cc.html_index_top>
</@cc.html_index_top>
<div class="index-con cf">
    <div class="con-left">
        <div class="news cf">
            <div class="focus fl">
                <ul class="bxslider">
                  <li><img src="${bbs_url}images/111.jpg" title="car1" /></li>
                  <li><img src="${bbs_url}images/112.jpg" title='car2'/></li>
                  <li><img src="${bbs_url}images//113.jpg" title='car3' /></li>
                  <li><img src="${bbs_url}images/114.jpg" title='car4' /></li>
                  <li><img src="${bbs_url}images/111.jpg" title="car1" /></li>
                  <li><img src="${bbs_url}images/112.jpg" title='car2'/></li>
                  <li><img src="${bbs_url}images//113.jpg" title='car3' /></li>
                  <li><img src="${bbs_url}images/114.jpg" title='car4' /></li>
                </ul>
            </div>
            <div class="hot">
                <h3 class='index-title bg2'>最近文章<a href="#">更多</a></h3>
                <div class="hot-news">
                    <ul>
                     <#if hotarticleLists?? && hotarticleLists?size gt 0>
						<#list hotarticleLists as hotlist>
                        <li><a href="${bbs_url}xpabc/article/show/${hotlist.articleid!}.htm">${hotlist.article_title}</a></li>
                        </#list>
                     </#if> 
                     </ul>

                </div>
            </div>
        </div>
        <div class="ads"><img src="${bbs_url}images/111.jpg" alt=""></div>
        
        <div class="modules cf">
        <#if articleLists?? && articleLists?size gt 0>
						<#list articleLists as list>
            <div class="module">
                <h3 class='index-title bg${list.id!}'>${list.platename!}<a href="${bbs_url}xpabc/index/tobbsList/${list.plateid!}/1.htm">更多</a> </h3>
                <ul>
                <#if  list.content?? && list.content?size gt 0>
                             <#list list.content as contentlist>
                    <li><a href="${bbs_url}xpabc/article/show/${contentlist.articleid!}.htm">${contentlist.article_title!}</a></li>
                            </#list>
                  </#if> 
                </ul>
            </div>
         </#list>
		</#if>
        </div>
    </div>
   <div class="con-right">
        <div class="con-right-box">
            <h3 class="index-title  bg2">推荐精华</h3>
            <ul class='con-right-list' style="height:355px">
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
      $('.bxslider').bxSlider({
        mode: 'fade',
        captions: true
    });

    $(document).ready(function(){
        $('h3#letterseq a').on('click',function(){
            $this=$(this).index();
             $(this).addClass('cur').siblings().removeClass('cur');
             $('.pf_blist .lettershow').eq($this).show().siblings().hide();
        })
    })
</script>
</@cc.html_js>