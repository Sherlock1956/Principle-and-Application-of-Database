package preparedstatement;

import java.sql.*;
import java.util.Scanner;

/**
 * ClassName: Experiment
 * Package: preparedstatement
 * Description:
 *
 * @Author 李宇潇
 * @Create 2023/12/29 17:25
 * @Version 1.0
 */
public class Experiment {
    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        boolean flag = true;
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection connection = DriverManager.getConnection("jdbc:mysql:///test", "root", "Mike855152");
        while (flag) {
            System.out.println("可查询列表如下：");
            System.out.println("1:显示过去n(自定)年各品牌的销售趋势，按年、月分列。然后将这些数据按购买者的性别和收入范围进行分类。");
            System.out.println("2:假设发现供应商“爱信”在两个给定日期之间进行的变速器存在缺陷。找到每辆装有这种变速器的汽车的车辆识别号（VIN）以及向其销售的客户。");
            System.out.println("3:根据过去n(自定)年的销售量找出前两大品牌。");
            System.out.println("4:某种车型（例如Golf Variant）在哪个月卖得最好？");
            System.out.println("5:找到平均库存时间最长的经销商。");
            System.out.print("请选择查询选项：");
            Scanner scanner = new Scanner(System.in);
            int select = scanner.nextInt();
            switch (select){
                case 1:{

                    System.out.print("请输入要获取过去多少年的信息：");
                    int year = scanner.nextInt();
                    String sql = "SELECT\n" +
                            "    YEAR(销售日期) AS 年份,\n" +
                            "    MONTH(销售日期) AS 月份,\n" +
                            "    品牌.品牌名称,\n" +
                            "    客户.性别,\n" +
                            "    客户.年收入,\n" +
                            "    CASE\n" +
                            "        WHEN 客户.年收入 < 180000 THEN '低收入'\n" +
                            "        WHEN 客户.年收入 BETWEEN 180000 AND 200000 THEN '中收入'\n" +
                            "        ELSE '高收入'\n" +
                            "    END AS 收入范围\n" +
                            "FROM\n" +
                            "    车辆\n" +
                            "JOIN\n" +
                            "    车型 ON 车辆.车型ID = 车型.车型ID\n" +
                            "JOIN\n" +
                            "    品牌 ON 车型.品牌ID = 品牌.品牌ID\n" +
                            "JOIN\n" +
                            "    客户 ON 车辆.客户ID = 客户.客户ID\n" +
                            "WHERE\n" +
                            "    销售日期 BETWEEN CURDATE() - INTERVAL ? YEAR AND CURDATE()\n" +
                            "ORDER BY 性别 DESC,收入范围 DESC;";
                    PreparedStatement preparedStatement = connection.prepareStatement(sql);
                    preparedStatement.setObject(1,year);
                    ResultSet resultSet = preparedStatement.executeQuery();
                    ResultSetMetaData metaData = resultSet.getMetaData();
                    int columnCount = metaData.getColumnCount();
                    for (int i = 1; i <= columnCount; i++) {
                        String columnLabel = metaData.getColumnLabel(i);
                        System.out.print(columnLabel);
                        if(i != columnCount)
                            System.out.print("---");
                    }
                    System.out.println();
                    while (resultSet.next()) {
                        for (int i = 1; i <= columnCount; i++) {
                            Object value = resultSet.getObject(i);
                            //不用getColumnName 只会获取列的名称，而Label会获取别名
                            System.out.print(value);
                            if(i != columnCount)
                                System.out.print("---");
                        }
                        System.out.println();
                    }
                    resultSet.close();
                    preparedStatement.close();
                    break;
                }
                case 2:{
                    String sql = "SELECT VIN,姓名,零件名称,供应商名称 FROM\n" +
                            "车辆 JOIN 车型 ON 车辆.`车型ID`=车型.`车型ID`\n" +
                            "JOIN 供应 ON 供应.`车型ID`=车辆.`车型ID`\n" +
                            "JOIN 供应商 ON 供应商.`供应商ID`=供应.`供应商ID`\n" +
                            "JOIN 客户 ON 客户.`客户ID`=车辆.`客户ID`\n" +
                            "WHERE 零件名称=\"变速器\" AND 供应商名称=\"爱信\";";
                    Statement statement = connection.createStatement();
                    ResultSet resultSet = statement.executeQuery(sql);
                    ResultSetMetaData metaData = resultSet.getMetaData();
                    int columnCount = metaData.getColumnCount();
                    for (int i = 1; i <= columnCount; i++) {
                        String columnLabel = metaData.getColumnLabel(i);
                        System.out.print(columnLabel);
                        if(i != columnCount)
                            System.out.print("---");
                    }
                    System.out.println();
                    while (resultSet.next()) {
                        for (int i = 1; i <= columnCount; i++) {
                            Object object = resultSet.getObject(i);
                            System.out.print(object);
                            if(i != columnCount)
                                System.out.print("---");
                        }
                        System.out.println();
                    }
                    resultSet.close();
                    statement.close();
                    break;
                }
                case 3:{
                    System.out.print("请输入要获取过去多少年的信息：");
                    int year = scanner.nextInt();
                    String sql = "SELECT 品牌名称,COUNT(*)销售量 FROM \n" +
                            "车辆 JOIN 车型 ON 车辆.`车型ID`=车型.`车型ID`\n" +
                            "JOIN 客户 ON 车辆.`客户ID`=客户.`客户ID`\n" +
                            "JOIN 品牌 ON 车型.`品牌ID`=品牌.`品牌ID`\n" +
                            "WHERE 销售日期 BETWEEN CURDATE() - INTERVAL ? YEAR AND CURDATE()\n" +
                            "GROUP BY 品牌名称\n" +
                            "ORDER BY 销售量 DESC;";
                    PreparedStatement preparedStatement = connection.prepareStatement(sql);
                    preparedStatement.setObject(1,year);
                    ResultSet resultSet = preparedStatement.executeQuery();
                    ResultSetMetaData metaData = resultSet.getMetaData();
                    int columnCount = metaData.getColumnCount();
                    for (int i = 1; i <= columnCount; i++) {
                        String columnLabel = metaData.getColumnLabel(i);
                        System.out.print(columnLabel);
                        if(i != columnCount)
                            System.out.print("---");
                    }
                    System.out.println();
                    while (resultSet.next()) {
                        for (int i = 1; i <= columnCount; i++) {
                            Object object = resultSet.getObject(i);
                            System.out.print(object);
                            if(i != columnCount)
                                System.out.print("---");
                        }
                        System.out.println();
                    }
                    resultSet.close();
                    preparedStatement.close();
                    break;
                }
                case 4:{
                    String sql = "SELECT MONTH(销售日期) 销售月份,COUNT(*) 销售量 FROM\n" +
                            "车辆 JOIN 车型 ON 车辆.`车型ID`=车型.`车型ID`\n" +
                            "JOIN 客户 ON 客户.`客户ID`=车辆.`客户ID`\n" +
                            "WHERE 车型名称=\"Golf Variant\"\n" +
                            "GROUP BY 销售月份\n" +
                            "ORDER BY 销售量 DESC;";
                    PreparedStatement preparedStatement = connection.prepareStatement(sql);
                    ResultSet resultSet = preparedStatement.executeQuery();
                    ResultSetMetaData metaData = resultSet.getMetaData();
                    int columnCount = metaData.getColumnCount();
                    for (int i = 1; i <= columnCount; i++) {
                        String columnLabel = metaData.getColumnLabel(i);
                        System.out.print(columnLabel);
                        if(i != columnCount)
                            System.out.print("---");
                    }
                    System.out.println();
                    while (resultSet.next()) {
                        for (int i = 1; i <= columnCount; i++) {
                            Object object = resultSet.getObject(i);
                            System.out.print(object);
                            if(i != columnCount)
                                System.out.print("---");
                        }
                        System.out.println();
                    }
                    resultSet.close();
                    preparedStatement.close();
                    break;
                }
                case 5:{
                    String sql = "SELECT 经销商名称,AVG(销售日期-出厂日期) 平均库存时间 FROM \n" +
                            "车辆 JOIN 车型 ON 车辆.`车型ID`=车型.`车型ID`\n" +
                            "JOIN 客户 ON 车辆.`客户ID`=客户.`客户ID`\n" +
                            "JOIN 经销商 ON 经销商.`经销商ID`=客户.`经销商ID`\n" +
                            "GROUP BY 经销商名称\n" +
                            "ORDER BY 平均库存时间 DESC;";
                    PreparedStatement preparedStatement = connection.prepareStatement(sql);
                    ResultSet resultSet = preparedStatement.executeQuery();
                    ResultSetMetaData metaData = resultSet.getMetaData();
                    int columnCount = metaData.getColumnCount();
                    for (int i = 1; i <= columnCount; i++) {
                        String columnLabel = metaData.getColumnLabel(i);
                        System.out.print(columnLabel);
                        if(i != columnCount)
                            System.out.print("---");
                    }
                    System.out.println();
                    while (resultSet.next()) {
                        for (int i = 1; i <= columnCount; i++) {
                            Object object = resultSet.getObject(i);
                            System.out.print(object);
                            if(i != columnCount)
                                System.out.print("---");
                        }
                        System.out.println();
                    }
                    resultSet.close();
                    preparedStatement.close();
                    break;
                }
            }
            System.out.print("是否继续查询：(y/n)");
            Scanner scanner1 = new Scanner(System.in);
            String next = scanner1.next();
            if("y".equals(next)){
                flag = true;
                System.out.println("------------------------------------");
            }
            else flag = false;
        }
        connection.close();
    }
}
