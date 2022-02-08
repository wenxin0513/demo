<table border='0' cellpadding='0' cellspacing='0' width='1817'
       style="font-weight: bold; font-size: 12px; font-family: 'Arial Unicode MS'; ">
  <caption>学生信息</caption>
<#-- 第一行，tr 中设置高：cm = pt/100 . td 设置宽-->
<#-- 转换后的数值只能是小数2位 -->
 <tr style='height:0.15cm'>
  <td style='width:0.02cm;'></td>
  <td style='width:0.09cm;'></td>
  <td style='width:0.18cm;'></td>
 </tr>
 <tr style='height:0.27cm'>
  <td colspan='3' style="font-weight: bold; text-align: center; font-size: 18px; font-family: 'Arial Unicode MS'">学生名单 </td>
 </tr>
 <tr style='height:0.17cm'>
  <td >主键id</td>
  <td >姓名</td>
  <td >性别</td>
 </tr>
 <#if student?? && (student?size> 0) >
  <#list student as item>
     <tr style='height:0.17cm'>
      <td >${item.id}</td>
      <td >${item.name}</td>
      <td>${item.sex}</td>
     </tr>
   </#list>
 </#if>
</table>

<table border='0' cellpadding='0' cellspacing='0' width='1817'
            style="font-weight: bold; font-size: 12px; font-family: 'Arial Unicode MS'; ">
  <caption>老师信息</caption>
    <tr style='height:0.15cm'>
        <td style='width:0.02cm;'></td>
        <td style='width:0.09cm;'></td>
        <td style='width:0.18cm;'></td>
    </tr>
   <tr style='height:0.27cm'>
     <td colspan='3' style="font-weight: bold; text-align: center; font-size: 18px; font-family: 'Arial Unicode MS'">老师名单</td>
   </tr>
  <tr style='height:0.17cm'>
      <td >主键id</td>
      <td >姓名</td>
      <td >性别</td>
  </tr>
 <#if teacher?? && (teacher?size> 0) >
    <#list teacher as item>
  <tr style='height:0.17cm'>
   <td >${item.id}</td>
   <td >${item.name}</td>
   <td>${item.sex}</td>
  </tr>
    </#list>
 </#if>
</table>


