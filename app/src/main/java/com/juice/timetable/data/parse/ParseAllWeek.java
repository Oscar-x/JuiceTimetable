package com.juice.timetable.data.parse;

import com.juice.timetable.data.bean.Course;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

/**
 * <pre>
 *     author : soreak
 *     e-mail : sorea1k@163.com
 *     time   : 2020/04/28
 *     desc   : nothing
 *     item   : juice
 *     version: 1.0
 * </pre>
 */

public class ParseAllWeek {
    private static List<Course> couList = new ArrayList<>();
    private static List<Course> courseList = new ArrayList<>();
    private static Integer onlyID = 0;

    /**
     * 解析完整的课表
     */
    public static List<Course> parseAllCourse() {
        //从文档中导入完整课表txt
        String s = getAllCourseStr2018();
        //String s = ReadFile.readToString("C:\\Users\\14989\\Desktop\\网页内容\\完整课表.html");
        //Jsoup解析
        Document document = Jsoup.parse(s);
        //从div中提取出文本，内容是课表与名字
        //String title = document.getElementsByTag("div").eq(0).text();
        //System.out.println(title);


        ///将table左边的表格标签里的内容提取（课程名，老师，起始结束周）
        Elements leftTable = document.getElementsByTag("td").eq(1);


        for (Element el : leftTable) {
            Integer len_Tr = el.getElementsByTag("tr").size();
            //System.out.println(len_Tr);
            for (int a = 1; a < len_Tr; a++) {
                Elements oneCourse = el.getElementsByTag("tr").eq(a);
                //System.out.println(oneCourse.html());
                for (Element ele : oneCourse) {
                    Course course = new Course();
                    if (ele.getElementsByTag("td").size() < 3) {
                        break;
                    }
                    String couName = ele.getElementsByTag("td").eq(0).text();

                    course.setCouName(couName);
                    //
                    if (!"".equals(ele.getElementsByTag("td").eq(2).text())) {
                        String couTeacher = ele.getElementsByTag("td").eq(2).text();
                        course.setCouTeacher(couTeacher);
                    }
                    if (!"".equals(ele.getElementsByTag("td").eq(10).text())) {
                        //System.out.println(ele.getElementsByTag("td").eq(10).text());
                        Integer couStartWeek = Integer.valueOf(ele.getElementsByTag("td").eq(10).text().split("～")[0]);
                        course.setCouStartWeek(couStartWeek);
                        Integer couEndWeek = Integer.valueOf(ele.getElementsByTag("td").eq(10).text().split("～")[1]);
                        course.setCouEndWeek(couEndWeek);
                    }
                    couList.add(course);


                }
            }
        }
        Long couID = 0L;
        for (Course cou : couList) {
//            System.out.println(cou);
            cou.setCouID(couID);
            couID++;

        }
        //将table左边的表格标签里的内容提取（）
        Elements rightTable = document.getElementsByTag("tbody").eq(3);

        for (Element element1 : rightTable) {
            Integer len_Tr1 = element1.getElementsByTag("tr").size();
            for (int l = 1; l < len_Tr1; l++) {
                Elements ele1 = element1.getElementsByTag("tr").eq(l);
                //System.out.println(ele1.html());
                for (Element el1 : ele1) {

                    Integer len_Td1 = el1.getElementsByTag("td").size();
                    for (int j = 1; j < len_Td1; j++) {
                        //去除为空的课程
                        if (!"".equals(el1.getElementsByTag("td").eq(j).text())) {
                            String tr = el1.getElementsByTag("td").eq(j).html();
                            //tr标签中td的数量
                            //String tr = el1.getElementsByTag("td").html();
                            //System.out.println(e1.html());
                            Integer len_Br = tr.split("<br>").length;
                            //System.out.println(tr.split("<br>")[0]+"======");
                            for (int a = 0; a < len_Br; a++) {
                                Course course = null;
                                if (tr.split("<br>")[a].contains("班")) {
                                    String couname = tr.split("<br>")[a];
                                    //使用list对课程名字进行判断，相同的名字存储在同一个list

                                    // 循环List 找到 本轮解析中对应的课程对象
                                    for (Course cou : couList) {
                                        String parseName = couname.replace(" ", "");
                                        String listCouName = cou.getCouName().replace(" ", "");
                                        if (parseName.equals(listCouName) && cou.getOnlyID() == null) {
                                            course = new Course(cou.getCouID(), cou.getCouName(), cou.getCouTeacher(), cou.getCouStartWeek(), cou.getCouEndWeek());
                                            course.setOnlyID(onlyID);
                                            onlyID++;
                                        }
                                    }
                                    // 如果没找到 跳出本轮循环
                                    if (course == null) {
                                        continue;
                                    }

                                    if (tr.split("<br>")[a + 1].contains("[单]")) {
                                        course.setCouWeekType(1);
                                        //使用list后，对是否已经输入过教室进行判断，无则输入，有则重新开一个list存储
                                        String couRoom = tr.split("<br>")[a + 1].substring(4, tr.split("<br>")[a + 1].length() - 1);
                                        course.setCouRoom(couRoom);
                                    } else if (tr.split("<br>")[a + 1].contains("[双]")) {
                                        course.setCouWeekType(2);
                                        String couRoom = tr.split("<br>")[a + 1].substring(4, tr.split("<br>")[a + 1].length() - 1);
                                        course.setCouRoom(couRoom);
                                    } else {
                                        course.setCouWeekType(0);
                                        String couRoom = tr.split("<br>")[a + 1].substring(1, tr.split("<br>")[a + 1].length() - 1);
                                        course.setCouRoom(couRoom);
                                    }
                                    //判断是否在右边表格中有起始结束周
                                    String[] trArr = tr.split("<br>");
                                    if (trArr.length - 1 >= a + 2) {
                                        if (tr.split("<br>")[a + 2].contains("周")) {
                                            String newWeek = tr.split("<br>")[a + 2].substring(1, tr.split("<br>")[a + 2].length() - 2);
                                            //System.out.println(newWeek);
                                            course.setCouStartWeek(Integer.valueOf(newWeek.split("-")[0]));
                                            course.setCouEndWeek(Integer.valueOf(newWeek.split("-")[1]));
                                        }
                                    }
                                    String id = el1.getElementsByTag("td").eq(j).attr("id");
                                    Integer couWeek = Integer.valueOf(id.substring(id.length() - 1, id.length()));
                                    course.setCouWeek(couWeek);
                                    Integer couStartNode = Integer.valueOf(id.substring(0, id.length() - 1));
                                    course.setCouStartNode(couStartNode);

                                    Integer time = Integer.valueOf(el1.getElementsByTag("td").eq(j).attr("rowspan"));
                                    Integer couEndNode = couStartNode + time - 1;
                                    course.setCouEndNode(couEndNode);
                                    courseList.add(course);
                                }
                            }

                        }

                    }


                }
            }
        }


        // 解析结束
        /*for (Course course : courseList) {
            System.out.println(course);
        }*/
        return courseList;
    }

    public static String getAllCourseStr() {
        return "\n" +
                "<META NAME=\"ROBOTS\" CONTENT=\"NOINDEX,NOFOLLOW\">\n" +
                "<META HTTP-EQUIV=\"pragma\" CONTENT=\"no-cache\">\n" +
                "<META http-equiv=\"cache-control\" content=\"no-cache\">\n" +
                "<META HTTP-EQUIV=\"expires\" CONTENT=\"0\">\n" +
                "<META http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">\n" +
                " \n" +
                "\n" +
                "<html>\n" +
                "<head>\n" +
                "<title>福州大学至诚学院课程表</title>\n" +
                "<meta http-equiv=\"Content-Type\" content=\"text/html; Charset=UTF-8\">\n" +
                "<style media=print>\n" +
                ".Noprint{display:none;}\n" +
                ".PageNext{page-break-after: always;}\n" +
                "</style>\n" +
                "\n" +
                "<style>\n" +
                "<!-- \n" +
                "table {\n" +
                "\tfont-size: 10pt;\n" +
                "}\n" +
                "td {\n" +
                "\tfont-size: 9pt;\n" +
                "}\n" +
                ".button {font-family: \"宋体\";font-size: 9pt;color: #00006a; height: 19}\n" +
                "-->\n" +
                "</style>\n" +
                "\n" +
                "<link href=\"../inc/style.css\" rel=\"stylesheet\" type=\"text/css\">\n" +
                "</head>\n" +
                "\n" +
                "<BODY onbeforeprint=\"w.style.display='none';\"  onafterprint=\"w.style.display='';\">\n" +
                "  <table width=\"100%\" border=\"0\" cellspacing=\"0\" id=\"w\" class=\"Noprint\">\n" +
                "  <tr><td align=\"center\">\n" +
                "<form name=\"form\" method=\"post\" action=\"kb_xs.asp\">\n" +
                "\t <input name=\"menu_no\" type=\"hidden\" value=\"\">\n" +
                "\t \n" +
                "&nbsp;查询学期：<input name=\"xn\" type=\"text\" size=\"4\" value=\"2019\">学年 \n" +
                "\t\t<select name=\"xq\">\n" +
                "\t\t\t<option value=\"下\">下</option> \n" +
                "\t\t\t<option value=\"\"></option>  \t\n" +
                "\t\t\t<option value=\"上\">上</option>\n" +
                "\t\t\t<option value=\"下\">下</option>\n" +
                "\t\t</select>\n" +
                "\t\t学期 <input type=\"submit\" name=\"Submit\" value=\"查询\" class=\"button\">\n" +
                "\t\t&nbsp;&nbsp;<input name=\"print\" type=\"button\" value=\" 打印 \" onClick=\"javascript:window.print();\" class=\"button\">\t\t\t \n" +
                "</form>\n" +
                "</td></tr></table>\t\n" +
                "\n" +
                "<div align=\"center\"><strong>福州大学至诚学院 2019下学期张三同学课程表</strong>(2020-4-28 14:45:12)</div>\n" +
                "<table width=\"880\" cellspacing=\"0\" cellpadding=\"0\" align=\"center\" border=\"0\" bordercolor=\"#111111\">\n" +
                "  <tr> \n" +
                "<!--      <td valign=\"top\"><table width=\"440\" height=\"400\" cellspacing=\"0\" cellpadding=\"1\" align=\"center\" style=\"border-collapse: collapse\" border=\"1\" bordercolor=\"#111111\">-->\n" +
                "    <td valign=\"top\"><table width=\"100%\" height=\"400\" cellspacing=\"0\" cellpadding=\"1\" align=\"center\" style=\"border-collapse: collapse\" border=\"1\" bordercolor=\"#111111\">\n" +
                "        <tr  height=\"30\"> \n" +
                "          <td align=\"center\">课程名称</td>\n" +
                "          <td align=\"center\">大纲/进度表</td>\n" +
                "          <td align=\"center\">任课教师</td>\n" +
                "\t\t  <td align=\"center\">选修类型</td>\n" +
                "\t\t  <td align=\"center\">考试类别</td>\t\t  \n" +
                "\t\t  <td align=\"center\">班级</td>\t\t  \n" +
                "\t\t  <td align=\"center\">学分</td>\n" +
                "          <td align=\"center\">总<br>学<br>时 </td>\n" +
                "          <td align=\"center\">周<br>学<br>时 </td>\n" +
                "          <td align=\"center\">实<br>验<br>学<br>时 </td>\n" +
                "          <td align=\"center\">起讫时间<br>周序<br>(星期)</td>\n" +
                "        </tr>\n" +
                "        \n" +
                "        <tr> \n" +
                "          <td  >&nbsp;形势与政策（六） (6)班</td>\n" +
                "          <td align=\"center\"><a href=\"../kkgl/kcjd/dgjdb.asp?lx=dg&kkxq=2019%E4%B8%8B&kcdm=18000106&kkhm=2019%E4%B8%8B18000106010\" target=\"_blank\">大纲</a>\n" +
                "\t\t  /<a href=\"../kkgl/kcjd/dgjdb.asp?lx=jdb&kkxq=2019%E4%B8%8B&kcdm=18000106&kkhm=2019%E4%B8%8B18000106010\" target=\"_blank\">进度表</a></td>\t\t\t\n" +
                "          <td align=\"center\">韩晞婷</td>\n" +
                "\t\t  <td align=\"center\">公共必修</td>\t\t  \n" +
                "\t\t  <td align=\"center\">正常考考试</td>\t\t\t\t\t  \n" +
                "\t\t  <td align=\"center\">6班</td>\t\t\t\t\t  \n" +
                "\t\t  <td align=\"center\">.5</td>\n" +
                "          <td align=\"center\">8</td>\n" +
                "          <td align=\"center\">2</td>\n" +
                "          <td align=\"center\"></td>\n" +
                "          <td align=\"center\">11～14</td>\t\n" +
                "        </tr>\n" +
                "       \n" +
                "        <tr> \n" +
                "          <td rowspan=\"2\"  >&nbsp;高级数据库技术 (1)班</td>\n" +
                "          <td align=\"center\"><a href=\"../kkgl/kcjd/dgjdb.asp?lx=dg&kkxq=2019%E4%B8%8B&kcdm=16111306&kkhm=2019%E4%B8%8B16111306001\" target=\"_blank\">大纲</a>\n" +
                "\t\t  /<a href=\"../kkgl/kcjd/dgjdb.asp?lx=jdb&kkxq=2019%E4%B8%8B&kcdm=16111306&kkhm=2019%E4%B8%8B16111306001\" target=\"_blank\">进度表</a></td>\t\t\t\n" +
                "          <td align=\"center\">杨雄</td>\n" +
                "\t\t  <td align=\"center\">专业方向2</td>\t\t  \n" +
                "\t\t  <td align=\"center\">正常考考试</td>\t\t\t\t\t  \n" +
                "\t\t  <td align=\"center\">2017级计算机1</td>\t\t\t\t\t  \n" +
                "\t\t  <td align=\"center\">3</td>\n" +
                "          <td align=\"center\">48</td>\n" +
                "          <td align=\"center\">4</td>\n" +
                "          <td align=\"center\">24</td>\n" +
                "          <td align=\"center\">01～14</td>\t\n" +
                "        </tr>\n" +
                "       \n" +
                "        <tr height=\"25\"><td align=\"left\" colspan=\"10\">★备注：022</td></tr>\n" +
                "       \n" +
                "        <tr> \n" +
                "          <td rowspan=\"2\"  >&nbsp;数据挖掘与分析 (1)班</td>\n" +
                "          <td align=\"center\"><a href=\"../kkgl/kcjd/dgjdb.asp?lx=dg&kkxq=2019%E4%B8%8B&kcdm=16111405&kkhm=2019%E4%B8%8B16111405001\" target=\"_blank\">大纲</a>\n" +
                "\t\t  /<a href=\"../kkgl/kcjd/dgjdb.asp?lx=jdb&kkxq=2019%E4%B8%8B&kcdm=16111405&kkhm=2019%E4%B8%8B16111405001\" target=\"_blank\">进度表</a></td>\t\t\t\n" +
                "          <td align=\"center\">杨雄</td>\n" +
                "\t\t  <td align=\"center\">专业方向2</td>\t\t  \n" +
                "\t\t  <td align=\"center\">正常考考试</td>\t\t\t\t\t  \n" +
                "\t\t  <td align=\"center\">2017级计算机1</td>\t\t\t\t\t  \n" +
                "\t\t  <td align=\"center\">2</td>\n" +
                "          <td align=\"center\">32</td>\n" +
                "          <td align=\"center\">4</td>\n" +
                "          <td align=\"center\">10</td>\n" +
                "          <td align=\"center\">01～08</td>\t\n" +
                "        </tr>\n" +
                "       \n" +
                "        <tr height=\"25\"><td align=\"left\" colspan=\"10\">★备注：022</td></tr>\n" +
                "       \n" +
                "        <tr> \n" +
                "          <td  >&nbsp;大数据应用开发 (1)班</td>\n" +
                "          <td align=\"center\"><a href=\"../kkgl/kcjd/dgjdb.asp?lx=dg&kkxq=2019%E4%B8%8B&kcdm=16111308&kkhm=2019%E4%B8%8B16111308001\" target=\"_blank\">大纲</a>\n" +
                "\t\t  /<a href=\"../kkgl/kcjd/dgjdb.asp?lx=jdb&kkxq=2019%E4%B8%8B&kcdm=16111308&kkhm=2019%E4%B8%8B16111308001\" target=\"_blank\">进度表</a></td>\t\t\t\n" +
                "          <td align=\"center\">马云莺</td>\n" +
                "\t\t  <td align=\"center\">专业素质课</td>\t\t  \n" +
                "\t\t  <td align=\"center\">正常考考试</td>\t\t\t\t\t  \n" +
                "\t\t  <td align=\"center\">2017级计算机1</td>\t\t\t\t\t  \n" +
                "\t\t  <td align=\"center\">2</td>\n" +
                "          <td align=\"center\">32</td>\n" +
                "          <td align=\"center\">4</td>\n" +
                "          <td align=\"center\">16</td>\n" +
                "          <td align=\"center\">01～08</td>\t\n" +
                "        </tr>\n" +
                "       \n" +
                "        <tr> \n" +
                "          <td  >&nbsp;软件工程 (1)班</td>\n" +
                "          <td align=\"center\"><a href=\"../kkgl/kcjd/dgjdb.asp?lx=dg&kkxq=2019%E4%B8%8B&kcdm=06111303&kkhm=2019%E4%B8%8B06111303001\" target=\"_blank\">大纲</a>\n" +
                "\t\t  /<a href=\"../kkgl/kcjd/dgjdb.asp?lx=jdb&kkxq=2019%E4%B8%8B&kcdm=06111303&kkhm=2019%E4%B8%8B06111303001\" target=\"_blank\">进度表</a></td>\t\t\t\n" +
                "          <td align=\"center\">张栋</td>\n" +
                "\t\t  <td align=\"center\">专业素质课</td>\t\t  \n" +
                "\t\t  <td align=\"center\">正常考考试</td>\t\t\t\t\t  \n" +
                "\t\t  <td align=\"center\">2017级计算机1</td>\t\t\t\t\t  \n" +
                "\t\t  <td align=\"center\">3</td>\n" +
                "          <td align=\"center\">48</td>\n" +
                "          <td align=\"center\">2</td>\n" +
                "          <td align=\"center\">18</td>\n" +
                "          <td align=\"center\">01～13</td>\t\n" +
                "        </tr>\n" +
                "       \n" +
                "        <tr> \n" +
                "          <td  >&nbsp;数据可视化与可视分析 (1)班</td>\n" +
                "          <td align=\"center\"><a href=\"../kkgl/kcjd/dgjdb.asp?lx=dg&kkxq=2019%E4%B8%8B&kcdm=17111403&kkhm=2019%E4%B8%8B17111403001\" target=\"_blank\">大纲</a>\n" +
                "\t\t  /<a href=\"../kkgl/kcjd/dgjdb.asp?lx=jdb&kkxq=2019%E4%B8%8B&kcdm=17111403&kkhm=2019%E4%B8%8B17111403001\" target=\"_blank\">进度表</a></td>\t\t\t\n" +
                "          <td align=\"center\">沈炎斌</td>\n" +
                "\t\t  <td align=\"center\">专业素质课</td>\t\t  \n" +
                "\t\t  <td align=\"center\">正常考考试</td>\t\t\t\t\t  \n" +
                "\t\t  <td align=\"center\">2017级计算机1</td>\t\t\t\t\t  \n" +
                "\t\t  <td align=\"center\">2</td>\n" +
                "          <td align=\"center\">32</td>\n" +
                "          <td align=\"center\">2</td>\n" +
                "          <td align=\"center\">16</td>\n" +
                "          <td align=\"center\">00～00</td>\t\n" +
                "        </tr>\n" +
                "       \n" +
                "        <tr> \n" +
                "          <td  >&nbsp;云计算与数据中心 (1)班</td>\n" +
                "          <td align=\"center\"><a href=\"../kkgl/kcjd/dgjdb.asp?lx=dg&kkxq=2019%E4%B8%8B&kcdm=16111407&kkhm=2019%E4%B8%8B16111407002\" target=\"_blank\">大纲</a>\n" +
                "\t\t  /<a href=\"../kkgl/kcjd/dgjdb.asp?lx=jdb&kkxq=2019%E4%B8%8B&kcdm=16111407&kkhm=2019%E4%B8%8B16111407002\" target=\"_blank\">进度表</a></td>\t\t\t\n" +
                "          <td align=\"center\">林庆新</td>\n" +
                "\t\t  <td align=\"center\">专业素质课</td>\t\t  \n" +
                "\t\t  <td align=\"center\">正常考考试</td>\t\t\t\t\t  \n" +
                "\t\t  <td align=\"center\">2017级计算机1</td>\t\t\t\t\t  \n" +
                "\t\t  <td align=\"center\">2</td>\n" +
                "          <td align=\"center\">32</td>\n" +
                "          <td align=\"center\">8</td>\n" +
                "          <td align=\"center\">12</td>\n" +
                "          <td align=\"center\">01～04</td>\t\n" +
                "        </tr>\n" +
                "       \n" +
                "        <tr> \n" +
                "          <td rowspan=\"2\"  >&nbsp;中国古典诗词中的品格与修养 (121)班</td>\n" +
                "          <td align=\"center\"><a href=\"../kkgl/kcjd/dgjdb.asp?lx=dg&kkxq=2019%E4%B8%8B&kcdm=80000117&kkhm=2019%E4%B8%8B80000117121\" target=\"_blank\">大纲</a>\n" +
                "\t\t  /<a href=\"../kkgl/kcjd/dgjdb.asp?lx=jdb&kkxq=2019%E4%B8%8B&kcdm=80000117&kkhm=2019%E4%B8%8B80000117121\" target=\"_blank\">进度表</a></td>\t\t\t\n" +
                "          <td align=\"center\"></td>\n" +
                "\t\t  <td align=\"center\">院选课</td>\t\t  \n" +
                "\t\t  <td align=\"center\">正常考考试</td>\t\t\t\t\t  \n" +
                "\t\t  <td align=\"center\">院选课智慧树</td>\t\t\t\t\t  \n" +
                "\t\t  <td align=\"center\">2</td>\n" +
                "          <td align=\"center\">32</td>\n" +
                "          <td align=\"center\"></td>\n" +
                "          <td align=\"center\"></td>\n" +
                "          <td align=\"center\"></td>\t\n" +
                "        </tr>\n" +
                "       \n" +
                "        <tr height=\"25\"><td align=\"left\" colspan=\"10\">★备注：期末成绩=平时学习40%+学院组织的线下考试60%</td></tr>\n" +
                "       \n" +
                "        <tr> \n" +
                "          <td rowspan=\"2\"  >&nbsp;大数据应用开发实践 (1)班</td>\n" +
                "          <td align=\"center\"><a href=\"../kkgl/kcjd/dgjdb.asp?lx=dg&kkxq=2019%E4%B8%8B&kcdm=16111603&kkhm=2019%E4%B8%8B16111603001\" target=\"_blank\">大纲</a>\n" +
                "\t\t  /<a href=\"../kkgl/kcjd/dgjdb.asp?lx=jdb&kkxq=2019%E4%B8%8B&kcdm=16111603&kkhm=2019%E4%B8%8B16111603001\" target=\"_blank\">进度表</a></td>\t\t\t\n" +
                "          <td align=\"center\">马云莺</td>\n" +
                "\t\t  <td align=\"center\">实践环节</td>\t\t  \n" +
                "\t\t  <td align=\"center\">正常考考试</td>\t\t\t\t\t  \n" +
                "\t\t  <td align=\"center\">2017级计算机1</td>\t\t\t\t\t  \n" +
                "\t\t  <td align=\"center\">1.5</td>\n" +
                "          <td align=\"center\">30</td>\n" +
                "          <td align=\"center\">4</td>\n" +
                "          <td align=\"center\"></td>\n" +
                "          <td align=\"center\">09～16</td>\t\n" +
                "        </tr>\n" +
                "       \n" +
                "        <tr height=\"25\"><td align=\"left\" colspan=\"10\">★备注：选修</td></tr>\n" +
                "       \n" +
                "        <tr> \n" +
                "          <td rowspan=\"2\"  >&nbsp;大数据综合应用案例实训 (1)班</td>\n" +
                "          <td align=\"center\"><a href=\"../kkgl/kcjd/dgjdb.asp?lx=dg&kkxq=2019%E4%B8%8B&kcdm=17111607&kkhm=2019%E4%B8%8B17111607001\" target=\"_blank\">大纲</a>\n" +
                "\t\t  /<a href=\"../kkgl/kcjd/dgjdb.asp?lx=jdb&kkxq=2019%E4%B8%8B&kcdm=17111607&kkhm=2019%E4%B8%8B17111607001\" target=\"_blank\">进度表</a></td>\t\t\t\n" +
                "          <td align=\"center\">马云莺</td>\n" +
                "\t\t  <td align=\"center\">实践环节</td>\t\t  \n" +
                "\t\t  <td align=\"center\">正常考考试</td>\t\t\t\t\t  \n" +
                "\t\t  <td align=\"center\">2017级计算机1</td>\t\t\t\t\t  \n" +
                "\t\t  <td align=\"center\">1.5</td>\n" +
                "          <td align=\"center\">30</td>\n" +
                "          <td align=\"center\">4</td>\n" +
                "          <td align=\"center\"></td>\n" +
                "          <td align=\"center\">05～11</td>\t\n" +
                "        </tr>\n" +
                "       \n" +
                "        <tr height=\"25\"><td align=\"left\" colspan=\"10\">★备注：选修</td></tr>\n" +
                "       \n" +
                "        <tr> \n" +
                "          <td rowspan=\"2\"  >&nbsp;数据挖掘应用实践 (1)班</td>\n" +
                "          <td align=\"center\"><a href=\"../kkgl/kcjd/dgjdb.asp?lx=dg&kkxq=2019%E4%B8%8B&kcdm=17111606&kkhm=2019%E4%B8%8B17111606001\" target=\"_blank\">大纲</a>\n" +
                "\t\t  /<a href=\"../kkgl/kcjd/dgjdb.asp?lx=jdb&kkxq=2019%E4%B8%8B&kcdm=17111606&kkhm=2019%E4%B8%8B17111606001\" target=\"_blank\">进度表</a></td>\t\t\t\n" +
                "          <td align=\"center\">杨雄</td>\n" +
                "\t\t  <td align=\"center\">实践环节</td>\t\t  \n" +
                "\t\t  <td align=\"center\">正常考考试</td>\t\t\t\t\t  \n" +
                "\t\t  <td align=\"center\">2017级计算机1</td>\t\t\t\t\t  \n" +
                "\t\t  <td align=\"center\">1.5</td>\n" +
                "          <td align=\"center\">30</td>\n" +
                "          <td align=\"center\">√</td>\n" +
                "          <td align=\"center\">4</td>\n" +
                "          <td align=\"center\">09～15</td>\t\n" +
                "        </tr>\n" +
                "       \n" +
                "        <tr height=\"25\"><td align=\"left\" colspan=\"10\">★备注：选修</td></tr>\n" +
                "       \n" +
                "      </table><font color=\"#CC3333\">\n" +
                "\t  </font>\n" +
                "\t  </td>\n" +
                "    <td valign=\"top\">\n" +
                "\t\n" +
                "\t<table width=\"440\" height=\"400\" cellspacing=\"0\" cellpadding=\"1\" align=\"center\" style=\"border-collapse: collapse\" border=\"1\" bordercolor=\"#111111\">\n" +
                "        <tr  height=\"30\"> \n" +
                "          <td align=\"center\">\n" +
                "            星期<br>\n" +
                "            节次 </td>\n" +
                "          <td align=\"center\">一</td>\n" +
                "          <td align=\"center\">二</td>\n" +
                "          <td align=\"center\">三</td>\n" +
                "          <td align=\"center\">四</td>\n" +
                "          <td align=\"center\">五</td>\n" +
                "\t\t  \n" +
                "        </tr>\n" +
                "        \n" +
                "        \n" +
                "        <tr id=\"tr1\"> \n" +
                "          <td align=\"center\">1</td>\n" +
                "          \n" +
                "          <td align=\"center\" rowspan=4 id=\"11\">高级数据库技术(1)班<br>[网络教学]</td>\n" +
                "          \n" +
                "          <td id=\"12\">&nbsp;</td>\n" +
                "          \n" +
                "          <td align=\"center\" rowspan=4 id=\"13\">数据挖掘与分析(1)班<br>[网络教学]<br>数据挖掘应用实践(1)班<br>[网络教学]<br>数据挖掘应用实践(1)班<br>[网络教学]<br>[1~2节]<br>(16-16周)</td>\n" +
                "          \n" +
                "          <td id=\"14\">&nbsp;</td>\n" +
                "          \n" +
                "          <td align=\"center\" rowspan=4 id=\"15\">云计算与数据中心(1)班<br>[网络教学]</td>\n" +
                "          \n" +
                "        </tr>\n" +
                "        \n" +
                "        <tr id=\"tr2\"> \n" +
                "          <td align=\"center\">2</td>\n" +
                "          \n" +
                "          <td id=\"22\">&nbsp;</td>\n" +
                "          \n" +
                "          <td id=\"24\">&nbsp;</td>\n" +
                "          \n" +
                "        </tr>\n" +
                "        \n" +
                "        <tr id=\"tr3\"> \n" +
                "          <td align=\"center\">3</td>\n" +
                "          \n" +
                "          <td align=\"center\" rowspan=2 id=\"32\">形势与政策（六）(6)班<br>[网络教学]</td>\n" +
                "          \n" +
                "          <td id=\"34\">&nbsp;</td>\n" +
                "          \n" +
                "        </tr>\n" +
                "        \n" +
                "        <tr id=\"tr4\"> \n" +
                "          <td align=\"center\">4</td>\n" +
                "          \n" +
                "          <td id=\"44\">&nbsp;</td>\n" +
                "          \n" +
                "        </tr>\n" +
                "        \n" +
                "        <tr id=\"tr5\"> \n" +
                "          <td align=\"center\">5</td>\n" +
                "          \n" +
                "          <td align=\"center\" rowspan=2 id=\"51\">软件工程(1)班<br>[网络教学]</td>\n" +
                "          \n" +
                "          <td align=\"center\" rowspan=4 id=\"52\">大数据应用开发(1)班<br>[网络教学]<br>大数据应用开发实践(1)班<br>[网络教学]<br>大数据应用开发实践(1)班<br>[网络教学]<br>[5~6节]<br>(17-17周)</td>\n" +
                "          \n" +
                "          <td align=\"center\" rowspan=4 id=\"53\">软件工程(1)班<br>[网络教学]<br>(05-08周)<br>软件工程(1)班<br>[网络教学]<br>(10-10周)<br>云计算与数据中心(1)班<br>[网络教学]</td>\n" +
                "          \n" +
                "          <td align=\"center\" rowspan=4 id=\"54\">大数据综合应用案例实训(1)班<br>[网络教学]<br>大数据综合应用案例实训(1)班<br>[网络教学]<br>[5~6节]<br>(13-13周)</td>\n" +
                "          \n" +
                "          <td id=\"55\">&nbsp;</td>\n" +
                "          \n" +
                "        </tr>\n" +
                "        \n" +
                "        <tr id=\"tr6\"> \n" +
                "          <td align=\"center\">6</td>\n" +
                "          \n" +
                "          <td id=\"65\">&nbsp;</td>\n" +
                "          \n" +
                "        </tr>\n" +
                "        \n" +
                "        <tr id=\"tr7\"> \n" +
                "          <td align=\"center\">7</td>\n" +
                "          \n" +
                "          <td id=\"71\">&nbsp;</td>\n" +
                "          \n" +
                "          <td id=\"75\">&nbsp;</td>\n" +
                "          \n" +
                "        </tr>\n" +
                "        \n" +
                "        <tr id=\"tr8\"> \n" +
                "          <td align=\"center\">8</td>\n" +
                "          \n" +
                "          <td id=\"81\">&nbsp;</td>\n" +
                "          \n" +
                "          <td id=\"85\">&nbsp;</td>\n" +
                "          \n" +
                "        </tr>\n" +
                "        \n" +
                "        <tr id=\"tr9\"> \n" +
                "          <td align=\"center\">9</td>\n" +
                "          \n" +
                "          <td id=\"91\">&nbsp;</td>\n" +
                "          \n" +
                "          <td id=\"92\">&nbsp;</td>\n" +
                "          \n" +
                "          <td id=\"93\">&nbsp;</td>\n" +
                "          \n" +
                "          <td id=\"94\">&nbsp;</td>\n" +
                "          \n" +
                "          <td id=\"95\">&nbsp;</td>\n" +
                "          \n" +
                "        </tr>\n" +
                "        \n" +
                "        <tr id=\"tr10\"> \n" +
                "          <td align=\"center\">10</td>\n" +
                "          \n" +
                "          <td id=\"101\">&nbsp;</td>\n" +
                "          \n" +
                "          <td id=\"102\">&nbsp;</td>\n" +
                "          \n" +
                "          <td id=\"103\">&nbsp;</td>\n" +
                "          \n" +
                "          <td id=\"104\">&nbsp;</td>\n" +
                "          \n" +
                "          <td id=\"105\">&nbsp;</td>\n" +
                "          \n" +
                "        </tr>\n" +
                "        \n" +
                "        <tr id=\"tr11\"> \n" +
                "          <td align=\"center\">11</td>\n" +
                "          \n" +
                "          <td id=\"111\">&nbsp;</td>\n" +
                "          \n" +
                "          <td id=\"112\">&nbsp;</td>\n" +
                "          \n" +
                "          <td id=\"113\">&nbsp;</td>\n" +
                "          \n" +
                "          <td id=\"114\">&nbsp;</td>\n" +
                "          \n" +
                "          <td id=\"115\">&nbsp;</td>\n" +
                "          \n" +
                "        </tr>\n" +
                "        \n" +
                "      </table></td>\n" +
                "  </tr>\n" +
                "</table>\n" +
                "\t\n" +
                "</body>\n" +
                "</html>\n" +
                "\n";
    }

    public static String getAllCourseStr2018() {
        return "\n" +
                "\n" +
                "<META NAME=\"ROBOTS\" CONTENT=\"NOINDEX,NOFOLLOW\">\n" +
                "<META HTTP-EQUIV=\"pragma\" CONTENT=\"no-cache\">\n" +
                "<META http-equiv=\"cache-control\" content=\"no-cache\">\n" +
                "<META HTTP-EQUIV=\"expires\" CONTENT=\"0\">\n" +
                "<META http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">\n" +
                " \n" +
                "\n" +
                "<html>\n" +
                "<head>\n" +
                "<title>福州大学至诚学院课程表</title>\n" +
                "<meta http-equiv=\"Content-Type\" content=\"text/html; Charset=UTF-8\">\n" +
                "<style media=print>\n" +
                ".Noprint{display:none;}\n" +
                ".PageNext{page-break-after: always;}\n" +
                "</style>\n" +
                "\n" +
                "<style>\n" +
                "<!-- \n" +
                "table {\n" +
                "\tfont-size: 10pt;\n" +
                "}\n" +
                "td {\n" +
                "\tfont-size: 9pt;\n" +
                "}\n" +
                ".button {font-family: \"宋体\";font-size: 9pt;color: #00006a; height: 19}\n" +
                "-->\n" +
                "</style>\n" +
                "\n" +
                "<link href=\"../inc/style.css\" rel=\"stylesheet\" type=\"text/css\">\n" +
                "</head>\n" +
                "\n" +
                "<BODY onbeforeprint=\"w.style.display='none';\"  onafterprint=\"w.style.display='';\">\n" +
                "  <table width=\"100%\" border=\"0\" cellspacing=\"0\" id=\"w\" class=\"Noprint\">\n" +
                "  <tr><td align=\"center\">\n" +
                "<form name=\"form\" method=\"post\" action=\"kb_xs.asp\">\n" +
                "\t <input name=\"menu_no\" type=\"hidden\" value=\"\">\n" +
                "\t \n" +
                "&nbsp;查询学期：<input name=\"xn\" type=\"text\" size=\"4\" value=\"2018\">学年 \n" +
                "\t\t<select name=\"xq\">\n" +
                "\t\t\t<option value=\"下\">下</option> \n" +
                "\t\t\t<option value=\"\"></option>  \t\n" +
                "\t\t\t<option value=\"上\">上</option>\n" +
                "\t\t\t<option value=\"下\">下</option>\n" +
                "\t\t</select>\n" +
                "\t\t学期 <input type=\"submit\" name=\"Submit\" value=\"查询\" class=\"button\">\n" +
                "\t\t&nbsp;&nbsp;<input name=\"print\" type=\"button\" value=\" 打印 \" onClick=\"javascript:window.print();\" class=\"button\">\t\t\t \n" +
                "</form>\n" +
                "</td></tr></table>\t\n" +
                "\n" +
                "<div align=\"center\"><strong>福州大学至诚学院 2018下学期蔡泽华同学课程表</strong>(2020-4-30 16:52:31)</div>\n" +
                "<table width=\"880\" cellspacing=\"0\" cellpadding=\"0\" align=\"center\" border=\"0\" bordercolor=\"#111111\">\n" +
                "  <tr> \n" +
                "<!--      <td valign=\"top\"><table width=\"440\" height=\"400\" cellspacing=\"0\" cellpadding=\"1\" align=\"center\" style=\"border-collapse: collapse\" border=\"1\" bordercolor=\"#111111\">-->\n" +
                "    <td valign=\"top\"><table width=\"100%\" height=\"400\" cellspacing=\"0\" cellpadding=\"1\" align=\"center\" style=\"border-collapse: collapse\" border=\"1\" bordercolor=\"#111111\">\n" +
                "        <tr  height=\"30\"> \n" +
                "          <td align=\"center\">课程名称</td>\n" +
                "          <td align=\"center\">大纲/进度表</td>\n" +
                "          <td align=\"center\">任课教师</td>\n" +
                "\t\t  <td align=\"center\">选修类型</td>\n" +
                "\t\t  <td align=\"center\">考试类别</td>\t\t  \n" +
                "\t\t  <td align=\"center\">班级</td>\t\t  \n" +
                "\t\t  <td align=\"center\">学分</td>\n" +
                "          <td align=\"center\">总<br>学<br>时 </td>\n" +
                "          <td align=\"center\">周<br>学<br>时 </td>\n" +
                "          <td align=\"center\">实<br>验<br>学<br>时 </td>\n" +
                "          <td align=\"center\">起讫时间<br>周序<br>(星期)</td>\n" +
                "        </tr>\n" +
                "        \n" +
                "        <tr> \n" +
                "          <td  >&nbsp;大学英语（四） (35)班</td>\n" +
                "          <td align=\"center\"><a href=\"../kkgl/kcjd/dgjdb.asp?lx=dg&kkxq=2018%E4%B8%8B&kcdm=06000108&kkhm=2018%E4%B8%8B06000108035\" target=\"_blank\">大纲</a>\n" +
                "\t\t  /<a href=\"../kkgl/kcjd/dgjdb.asp?lx=jdb&kkxq=2018%E4%B8%8B&kcdm=06000108&kkhm=2018%E4%B8%8B06000108035\" target=\"_blank\">进度表</a></td>\t\t\t\n" +
                "          <td align=\"center\">林丽珍</td>\n" +
                "\t\t  <td align=\"center\">公共必修</td>\t\t  \n" +
                "\t\t  <td align=\"center\">正常考考试</td>\t\t\t\t\t  \n" +
                "\t\t  <td align=\"center\">35班</td>\t\t\t\t\t  \n" +
                "\t\t  <td align=\"center\">3</td>\n" +
                "          <td align=\"center\">48</td>\n" +
                "          <td align=\"center\">3</td>\n" +
                "          <td align=\"center\"></td>\n" +
                "          <td align=\"center\">01～16</td>\t\n" +
                "        </tr>\n" +
                "       \n" +
                "        <tr> \n" +
                "          <td  >&nbsp;概率论与数理统计 (10)班</td>\n" +
                "          <td align=\"center\"><a href=\"../kkgl/kcjd/dgjdb.asp?lx=dg&kkxq=2018%E4%B8%8B&kcdm=09000102&kkhm=2018%E4%B8%8B09000102010\" target=\"_blank\">大纲</a>\n" +
                "\t\t  /<a href=\"../kkgl/kcjd/dgjdb.asp?lx=jdb&kkxq=2018%E4%B8%8B&kcdm=09000102&kkhm=2018%E4%B8%8B09000102010\" target=\"_blank\">进度表</a></td>\t\t\t\n" +
                "          <td align=\"center\">陈江彬</td>\n" +
                "\t\t  <td align=\"center\">公共必修</td>\t\t  \n" +
                "\t\t  <td align=\"center\">正常考考试</td>\t\t\t\t\t  \n" +
                "\t\t  <td align=\"center\">10班</td>\t\t\t\t\t  \n" +
                "\t\t  <td align=\"center\">3</td>\n" +
                "          <td align=\"center\">54</td>\n" +
                "          <td align=\"center\">4</td>\n" +
                "          <td align=\"center\"></td>\n" +
                "          <td align=\"center\">01～15</td>\t\n" +
                "        </tr>\n" +
                "       \n" +
                "        <tr> \n" +
                "          <td  >&nbsp;马克思主义基本原理概论 (19)班</td>\n" +
                "          <td align=\"center\"><a href=\"../kkgl/kcjd/dgjdb.asp?lx=dg&kkxq=2018%E4%B8%8B&kcdm=07004113&kkhm=2018%E4%B8%8B07004113019\" target=\"_blank\">大纲</a>\n" +
                "\t\t  /<a href=\"../kkgl/kcjd/dgjdb.asp?lx=jdb&kkxq=2018%E4%B8%8B&kcdm=07004113&kkhm=2018%E4%B8%8B07004113019\" target=\"_blank\">进度表</a></td>\t\t\t\n" +
                "          <td align=\"center\">袁小云,郑夏妍</td>\n" +
                "\t\t  <td align=\"center\">公共必修</td>\t\t  \n" +
                "\t\t  <td align=\"center\">正常考考试</td>\t\t\t\t\t  \n" +
                "\t\t  <td align=\"center\">19班,周5 5~7</td>\t\t\t\t\t  \n" +
                "\t\t  <td align=\"center\">3</td>\n" +
                "          <td align=\"center\">48</td>\n" +
                "          <td align=\"center\"></td>\n" +
                "          <td align=\"center\"></td>\n" +
                "          <td align=\"center\">01～16</td>\t\n" +
                "        </tr>\n" +
                "       \n" +
                "        <tr> \n" +
                "          <td  >&nbsp;体育（四） (31)班</td>\n" +
                "          <td align=\"center\"><a href=\"../kkgl/kcjd/dgjdb.asp?lx=dg&kkxq=2018%E4%B8%8B&kcdm=06000125&kkhm=2018%E4%B8%8B06000125031\" target=\"_blank\">大纲</a>\n" +
                "\t\t  /<a href=\"../kkgl/kcjd/dgjdb.asp?lx=jdb&kkxq=2018%E4%B8%8B&kcdm=06000125&kkhm=2018%E4%B8%8B06000125031\" target=\"_blank\">进度表</a></td>\t\t\t\n" +
                "          <td align=\"center\">李粲</td>\n" +
                "\t\t  <td align=\"center\">公共必修</td>\t\t  \n" +
                "\t\t  <td align=\"center\">正常考考试</td>\t\t\t\t\t  \n" +
                "\t\t  <td align=\"center\">31班,周3,3-4节,排球（男）</td>\t\t\t\t\t  \n" +
                "\t\t  <td align=\"center\">1</td>\n" +
                "          <td align=\"center\">36</td>\n" +
                "          <td align=\"center\"></td>\n" +
                "          <td align=\"center\"></td>\n" +
                "          <td align=\"center\">02～15</td>\t\n" +
                "        </tr>\n" +
                "       \n" +
                "        <tr> \n" +
                "          <td  >&nbsp;形势与政策（四） (16)班</td>\n" +
                "          <td align=\"center\"><a href=\"../kkgl/kcjd/dgjdb.asp?lx=dg&kkxq=2018%E4%B8%8B&kcdm=08000106&kkhm=2018%E4%B8%8B08000106016\" target=\"_blank\">大纲</a>\n" +
                "\t\t  /<a href=\"../kkgl/kcjd/dgjdb.asp?lx=jdb&kkxq=2018%E4%B8%8B&kcdm=08000106&kkhm=2018%E4%B8%8B08000106016\" target=\"_blank\">进度表</a></td>\t\t\t\n" +
                "          <td align=\"center\">孙大为</td>\n" +
                "\t\t  <td align=\"center\">公共必修</td>\t\t  \n" +
                "\t\t  <td align=\"center\">正常考考试</td>\t\t\t\t\t  \n" +
                "\t\t  <td align=\"center\">16班,周四,1-2节</td>\t\t\t\t\t  \n" +
                "\t\t  <td align=\"center\">.5</td>\n" +
                "          <td align=\"center\">8</td>\n" +
                "          <td align=\"center\"></td>\n" +
                "          <td align=\"center\"></td>\n" +
                "          <td align=\"center\">15～18</td>\t\n" +
                "        </tr>\n" +
                "       \n" +
                "        <tr> \n" +
                "          <td  >&nbsp;计算机组成原理 (2)班</td>\n" +
                "          <td align=\"center\"><a href=\"../kkgl/kcjd/dgjdb.asp?lx=dg&kkxq=2018%E4%B8%8B&kcdm=10111203&kkhm=2018%E4%B8%8B10111203002\" target=\"_blank\">大纲</a>\n" +
                "\t\t  /<a href=\"../kkgl/kcjd/dgjdb.asp?lx=jdb&kkxq=2018%E4%B8%8B&kcdm=10111203&kkhm=2018%E4%B8%8B10111203002\" target=\"_blank\">进度表</a></td>\t\t\t\n" +
                "          <td align=\"center\">马云莺</td>\n" +
                "\t\t  <td align=\"center\">专业基础</td>\t\t  \n" +
                "\t\t  <td align=\"center\">正常考考试</td>\t\t\t\t\t  \n" +
                "\t\t  <td align=\"center\">2017级计算机2</td>\t\t\t\t\t  \n" +
                "\t\t  <td align=\"center\">4</td>\n" +
                "          <td align=\"center\">64</td>\n" +
                "          <td align=\"center\">4</td>\n" +
                "          <td align=\"center\">*</td>\n" +
                "          <td align=\"center\">01～17</td>\t\n" +
                "        </tr>\n" +
                "       \n" +
                "        <tr> \n" +
                "          <td  >&nbsp;算法与数据结构 (2)班</td>\n" +
                "          <td align=\"center\"><a href=\"../kkgl/kcjd/dgjdb.asp?lx=dg&kkxq=2018%E4%B8%8B&kcdm=06111206&kkhm=2018%E4%B8%8B06111206002\" target=\"_blank\">大纲</a>\n" +
                "\t\t  /<a href=\"../kkgl/kcjd/dgjdb.asp?lx=jdb&kkxq=2018%E4%B8%8B&kcdm=06111206&kkhm=2018%E4%B8%8B06111206002\" target=\"_blank\">进度表</a></td>\t\t\t\n" +
                "          <td align=\"center\">杨雄</td>\n" +
                "\t\t  <td align=\"center\">专业基础</td>\t\t  \n" +
                "\t\t  <td align=\"center\">正常考考试</td>\t\t\t\t\t  \n" +
                "\t\t  <td align=\"center\">2017级计算机2</td>\t\t\t\t\t  \n" +
                "\t\t  <td align=\"center\">4.5</td>\n" +
                "          <td align=\"center\">72</td>\n" +
                "          <td align=\"center\">5</td>\n" +
                "          <td align=\"center\">24</td>\n" +
                "          <td align=\"center\">01～16</td>\t\n" +
                "        </tr>\n" +
                "       \n" +
                "        <tr> \n" +
                "          <td  >&nbsp;面向对象程序设计 (1)班</td>\n" +
                "          <td align=\"center\"><a href=\"../kkgl/kcjd/dgjdb.asp?lx=dg&kkxq=2018%E4%B8%8B&kcdm=06111305&kkhm=2018%E4%B8%8B06111305001\" target=\"_blank\">大纲</a>\n" +
                "\t\t  /<a href=\"../kkgl/kcjd/dgjdb.asp?lx=jdb&kkxq=2018%E4%B8%8B&kcdm=06111305&kkhm=2018%E4%B8%8B06111305001\" target=\"_blank\">进度表</a></td>\t\t\t\n" +
                "          <td align=\"center\">杨晓花</td>\n" +
                "\t\t  <td align=\"center\">专业素质课</td>\t\t  \n" +
                "\t\t  <td align=\"center\">正常考考试</td>\t\t\t\t\t  \n" +
                "\t\t  <td align=\"center\">2017级计算机1</td>\t\t\t\t\t  \n" +
                "\t\t  <td align=\"center\">3</td>\n" +
                "          <td align=\"center\">48</td>\n" +
                "          <td align=\"center\">3</td>\n" +
                "          <td align=\"center\">16</td>\n" +
                "          <td align=\"center\">01～17</td>\t\n" +
                "        </tr>\n" +
                "       \n" +
                "        <tr> \n" +
                "          <td rowspan=\"2\"  >&nbsp;计算机组成原理实验 (2)班</td>\n" +
                "          <td align=\"center\"><a href=\"../kkgl/kcjd/dgjdb.asp?lx=dg&kkxq=2018%E4%B8%8B&kcdm=07102603&kkhm=2018%E4%B8%8B07102603002\" target=\"_blank\">大纲</a>\n" +
                "\t\t  /<a href=\"../kkgl/kcjd/dgjdb.asp?lx=jdb&kkxq=2018%E4%B8%8B&kcdm=07102603&kkhm=2018%E4%B8%8B07102603002\" target=\"_blank\">进度表</a></td>\t\t\t\n" +
                "          <td align=\"center\">马云莺</td>\n" +
                "\t\t  <td align=\"center\">实践环节</td>\t\t  \n" +
                "\t\t  <td align=\"center\">正常考考试</td>\t\t\t\t\t  \n" +
                "\t\t  <td align=\"center\">2017级计算机2</td>\t\t\t\t\t  \n" +
                "\t\t  <td align=\"center\">1</td>\n" +
                "          <td align=\"center\">24</td>\n" +
                "          <td align=\"center\">√</td>\n" +
                "          <td align=\"center\"></td>\n" +
                "          <td align=\"center\">10～15</td>\t\n" +
                "        </tr>\n" +
                "       \n" +
                "        <tr height=\"25\"><td align=\"left\" colspan=\"10\">★备注：必修，具体时间由任课教师分批进行</td></tr>\n" +
                "       \n" +
                "        <tr> \n" +
                "          <td  >&nbsp;面向对象程序设计实训 (1)班</td>\n" +
                "          <td align=\"center\"><a href=\"../kkgl/kcjd/dgjdb.asp?lx=dg&kkxq=2018%E4%B8%8B&kcdm=10111605&kkhm=2018%E4%B8%8B10111605003\" target=\"_blank\">大纲</a>\n" +
                "\t\t  /<a href=\"../kkgl/kcjd/dgjdb.asp?lx=jdb&kkxq=2018%E4%B8%8B&kcdm=10111605&kkhm=2018%E4%B8%8B10111605003\" target=\"_blank\">进度表</a></td>\t\t\t\n" +
                "          <td align=\"center\">杨晓花</td>\n" +
                "\t\t  <td align=\"center\">实践环节</td>\t\t  \n" +
                "\t\t  <td align=\"center\">正常考考试</td>\t\t\t\t\t  \n" +
                "\t\t  <td align=\"center\">2017级计算机1</td>\t\t\t\t\t  \n" +
                "\t\t  <td align=\"center\">1.5</td>\n" +
                "          <td align=\"center\">30</td>\n" +
                "          <td align=\"center\"></td>\n" +
                "          <td align=\"center\"></td>\n" +
                "          <td align=\"center\">08～15</td>\t\n" +
                "        </tr>\n" +
                "       \n" +
                "        <tr> \n" +
                "          <td  >&nbsp;思想政治理论课实践(二)</td>\n" +
                "          <td align=\"center\"><a href=\"../kkgl/kcjd/dgjdb.asp?lx=dg&kkxq=2018%E4%B8%8B&kcdm=07004608&kkhm=2018%E4%B8%8B07004608020\" target=\"_blank\">大纲</a>\n" +
                "\t\t  /<a href=\"../kkgl/kcjd/dgjdb.asp?lx=jdb&kkxq=2018%E4%B8%8B&kcdm=07004608&kkhm=2018%E4%B8%8B07004608020\" target=\"_blank\">进度表</a></td>\t\t\t\n" +
                "          <td align=\"center\">王海霞</td>\n" +
                "\t\t  <td align=\"center\">实践环节</td>\t\t  \n" +
                "\t\t  <td align=\"center\">正常考考试</td>\t\t\t\t\t  \n" +
                "\t\t  <td align=\"center\">2017计算机</td>\t\t\t\t\t  \n" +
                "\t\t  <td align=\"center\">1</td>\n" +
                "          <td align=\"center\"></td>\n" +
                "          <td align=\"center\"></td>\n" +
                "          <td align=\"center\"></td>\n" +
                "          <td align=\"center\"></td>\t\n" +
                "        </tr>\n" +
                "       \n" +
                "      </table><font color=\"#CC3333\">\n" +
                "\t  </font>\n" +
                "\t  </td>\n" +
                "    <td valign=\"top\">\n" +
                "\t\n" +
                "\t<table width=\"440\" height=\"400\" cellspacing=\"0\" cellpadding=\"1\" align=\"center\" style=\"border-collapse: collapse\" border=\"1\" bordercolor=\"#111111\">\n" +
                "        <tr  height=\"30\"> \n" +
                "          <td align=\"center\">\n" +
                "            星期<br>\n" +
                "            节次 </td>\n" +
                "          <td align=\"center\">一</td>\n" +
                "          <td align=\"center\">二</td>\n" +
                "          <td align=\"center\">三</td>\n" +
                "          <td align=\"center\">四</td>\n" +
                "          <td align=\"center\">五</td>\n" +
                "\t\t  \n" +
                "        </tr>\n" +
                "        \n" +
                "        \n" +
                "        <tr id=\"tr1\"> \n" +
                "          <td align=\"center\">1</td>\n" +
                "          \n" +
                "          <td id=\"11\">&nbsp;</td>\n" +
                "          \n" +
                "          <td align=\"center\" rowspan=2 id=\"12\">概率论与数理统计(10)班<br>[东教401]</td>\n" +
                "          \n" +
                "          <td align=\"center\" rowspan=2 id=\"13\">大学英语（四）(35)班<br>[东教108]</td>\n" +
                "          \n" +
                "          <td align=\"center\" rowspan=2 id=\"14\">概率论与数理统计(10)班<br>[化工314]<br>形势与政策（四）(16)班<br>[机北306]</td>\n" +
                "          \n" +
                "          <td id=\"15\">&nbsp;</td>\n" +
                "          \n" +
                "        </tr>\n" +
                "        \n" +
                "        <tr id=\"tr2\"> \n" +
                "          <td align=\"center\">2</td>\n" +
                "          \n" +
                "          <td id=\"21\">&nbsp;</td>\n" +
                "          \n" +
                "          <td id=\"25\">&nbsp;</td>\n" +
                "          \n" +
                "        </tr>\n" +
                "        \n" +
                "        <tr id=\"tr3\"> \n" +
                "          <td align=\"center\">3</td>\n" +
                "          \n" +
                "          <td align=\"center\" rowspan=2 id=\"31\">算法与数据结构(2)班<br>[轻工614]<br>(05-16周)</td>\n" +
                "          \n" +
                "          <td align=\"center\" rowspan=2 id=\"32\">大学英语（四）(35)班<br>[单][电机楼502]</td>\n" +
                "          \n" +
                "          <td align=\"center\" rowspan=2 id=\"33\">体育（四）(31)班<br>[体育场]</td>\n" +
                "          \n" +
                "          <td align=\"center\" rowspan=2 id=\"34\">算法与数据结构(2)班<br>[双][化工317]</td>\n" +
                "          \n" +
                "          <td align=\"center\" rowspan=2 id=\"35\">计算机组成原理(2)班<br>[化工317]</td>\n" +
                "          \n" +
                "        </tr>\n" +
                "        \n" +
                "        <tr id=\"tr4\"> \n" +
                "          <td align=\"center\">4</td>\n" +
                "          \n" +
                "        </tr>\n" +
                "        \n" +
                "        <tr id=\"tr5\"> \n" +
                "          <td align=\"center\">5</td>\n" +
                "          \n" +
                "          <td align=\"center\" rowspan=2 id=\"51\">面向对象程序设计(1)班<br>[化工304]</td>\n" +
                "          \n" +
                "          <td align=\"center\" rowspan=2 id=\"52\">计算机组成原理(2)班<br>[化工304]</td>\n" +
                "          \n" +
                "          <td align=\"center\" rowspan=2 id=\"53\">面向对象程序设计(1)班<br>[轻工616]<br>(04-07周)<br>面向对象程序设计(1)班<br>[化工314]<br>(01-03周)<br>面向对象程序设计实训(1)班<br>[双][轻工616]<br>[5~8节]<br>面向对象程序设计实训(1)班<br>[轻工613]<br>[5~8节]<br>(16-17周)</td>\n" +
                "          \n" +
                "          <td id=\"54\">&nbsp;</td>\n" +
                "          \n" +
                "          <td align=\"center\" rowspan=3 id=\"55\">马克思主义基本原理概论(19)班<br>[东教301]</td>\n" +
                "          \n" +
                "        </tr>\n" +
                "        \n" +
                "        <tr id=\"tr6\"> \n" +
                "          <td align=\"center\">6</td>\n" +
                "          \n" +
                "          <td id=\"64\">&nbsp;</td>\n" +
                "          \n" +
                "        </tr>\n" +
                "        \n" +
                "        <tr id=\"tr7\"> \n" +
                "          <td align=\"center\">7</td>\n" +
                "          \n" +
                "          <td id=\"71\">&nbsp;</td>\n" +
                "          \n" +
                "          <td align=\"center\" rowspan=2 id=\"72\">算法与数据结构(2)班<br>[化工317]</td>\n" +
                "          \n" +
                "          <td id=\"73\">&nbsp;</td>\n" +
                "          \n" +
                "          <td id=\"74\">&nbsp;</td>\n" +
                "          \n" +
                "        </tr>\n" +
                "        \n" +
                "        <tr id=\"tr8\"> \n" +
                "          <td align=\"center\">8</td>\n" +
                "          \n" +
                "          <td id=\"81\">&nbsp;</td>\n" +
                "          \n" +
                "          <td id=\"83\">&nbsp;</td>\n" +
                "          \n" +
                "          <td id=\"84\">&nbsp;</td>\n" +
                "          \n" +
                "          <td id=\"85\">&nbsp;</td>\n" +
                "          \n" +
                "        </tr>\n" +
                "        \n" +
                "        <tr id=\"tr9\"> \n" +
                "          <td align=\"center\">9</td>\n" +
                "          \n" +
                "          <td id=\"91\">&nbsp;</td>\n" +
                "          \n" +
                "          <td id=\"92\">&nbsp;</td>\n" +
                "          \n" +
                "          <td align=\"center\" rowspan=3 id=\"93\">面向对象程序设计实训(1)班<br>[单][轻工616]</td>\n" +
                "          \n" +
                "          <td id=\"94\">&nbsp;</td>\n" +
                "          \n" +
                "          <td align=\"center\" rowspan=3 id=\"95\">概率论与数理统计(10)班<br>[化工314]<br>(16-16周)</td>\n" +
                "          \n" +
                "        </tr>\n" +
                "        \n" +
                "        <tr id=\"tr10\"> \n" +
                "          <td align=\"center\">10</td>\n" +
                "          \n" +
                "          <td id=\"101\">&nbsp;</td>\n" +
                "          \n" +
                "          <td id=\"102\">&nbsp;</td>\n" +
                "          \n" +
                "          <td id=\"104\">&nbsp;</td>\n" +
                "          \n" +
                "        </tr>\n" +
                "        \n" +
                "        <tr id=\"tr11\"> \n" +
                "          <td align=\"center\">11</td>\n" +
                "          \n" +
                "          <td id=\"111\">&nbsp;</td>\n" +
                "          \n" +
                "          <td id=\"112\">&nbsp;</td>\n" +
                "          \n" +
                "          <td id=\"114\">&nbsp;</td>\n" +
                "          \n" +
                "        </tr>\n" +
                "        \n" +
                "      </table></td>\n" +
                "  </tr>\n" +
                "</table>\n" +
                "\t\n" +
                "</body>\n" +
                "</html>\n";
    }
}
