package com.example.station.config;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;

import java.sql.*;

/**
 * MySQL GEOMETRY 类型的 MyBatis TypeHandler
 * 用于在 Java String 和 MySQL GEOMETRY 之间进行转换
 */
@MappedTypes(String.class)
public class GeometryTypeHandler extends BaseTypeHandler<String> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, String parameter, JdbcType jdbcType) throws SQLException {
        // 将 WKT 格式的字符串（如 "POINT(113.9431 22.5411)"）转换为 GEOMETRY
        // 使用 ST_GeomFromText 函数
        ps.setString(i, parameter);
    }

    @Override
    public String getNullableResult(ResultSet rs, String columnName) throws SQLException {
        // 从数据库读取 GEOMETRY，转换为 WKT 格式字符串
        // 使用 ST_AsText 函数（在 SQL 中已处理）
        return rs.getString(columnName);
    }

    @Override
    public String getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return rs.getString(columnIndex);
    }

    @Override
    public String getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return cs.getString(columnIndex);
    }
}
