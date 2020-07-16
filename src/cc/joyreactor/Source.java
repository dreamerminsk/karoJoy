package cc.joyreactor;

import cc.joyreactor.data.Image;
import cc.joyreactor.data.Post;
import cc.joyreactor.data.Tag;
import cc.joyreactor.data.User;
import org.jdesktop.swingx.JXErrorPane;
import org.jdesktop.swingx.error.ErrorInfo;
import org.sqlite.JDBC;
import org.sqlite.util.StringUtils;

import java.math.BigDecimal;
import java.sql.*;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class Source {

    private static final String CONNECTION_STRING = "jdbc:sqlite:joyreactor.cc";

    private static final AtomicReference<Source> INSTANCE = new AtomicReference<>();
    private final Connection connection;

    private Source() throws SQLException {
        DriverManager.registerDriver(new JDBC());

        connection = DriverManager.getConnection(CONNECTION_STRING);
        //init();
    }

    public static Source getInstance() throws SQLException {
        if (INSTANCE.get() == null) INSTANCE.set(new Source());
        return INSTANCE.get();
    }


    public List<Tag> getTags() {
        List<Tag> tags = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM jr_tags 256;")) {
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    tags.add(new Tag(
                            rs.getInt("tag_id"),
                            rs.getString("tag"),
                            rs.getString("ref"),
                            rs.getString("ids"),
                            rs.getBytes("tag_avatar"),
                            rs.getBytes("tag_banner")));
                }
            }
        } catch (SQLException ex) {
            //JXErrorPane.showDialog(ex);
            System.out.println(ex.getClass().getName() + " - " + ex.getMessage());
            return tags;
        }
        return tags;
    }

    public List<Tag> getTagsWithoutBanner() {
        List<Tag> tags = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM jr_tags WHERE tag_banner IS NULL;")) {
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    tags.add(new Tag(
                            rs.getInt("tag_id"),
                            rs.getString("tag"),
                            rs.getString("ref"),
                            rs.getString("ids"),
                            rs.getBytes("tag_avatar"),
                            rs.getBytes("tag_banner")));
                }
            }
        } catch (SQLException ex) {
            //JXErrorPane.showDialog(ex);
            System.out.println(ex.getClass().getName() + " - " + ex.getMessage());
            return tags;
        }
        return tags;
    }

    public Tag getTag(String word) {
        try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM jr_tags WHERE tag=?;")) {
            statement.setString(1, word);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return new Tag(
                            rs.getInt("tag_id"),
                            rs.getString("tag"),
                            rs.getString("ref"),
                            rs.getString("ids"),
                            rs.getBytes("tag_avatar"),
                            rs.getBytes("tag_banner"));
                }
            }
        } catch (SQLException ex) {
            //JXErrorPane.showDialog(ex);
            System.out.println(ex.getClass().getName() + " - " + ex.getMessage());
            return null;
        }
        return null;
    }

    public int updateTag(Tag tag) {
        try (PreparedStatement statement = connection.prepareStatement(
                "UPDATE jr_tags SET tag=?, ref=?, ids=?, tag_avatar=?, tag_banner=? WHERE tag_id=?")) {
            statement.setString(1, tag.getTag());
            statement.setString(2, tag.getRef());
            statement.setString(3, tag.getIds());
            statement.setBytes(4, tag.getAvatar());
            statement.setBytes(5, tag.getBanner());
            statement.setInt(6, tag.getId());
            return statement.executeUpdate();
        } catch (SQLException ex) {
            //JXErrorPane.showDialog(ex);
            return 0;
        }
    }

    public int insertTag(Tag tag) {
        try (PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO jr_tags(tag, ref, ids, tag_avatar, tag_banner) VALUES(?, ?, ?, ?, ?)")) {
            statement.setString(1, tag.getTag());
            statement.setString(2, tag.getRef());
            statement.setString(3, tag.getIds());
            statement.setBytes(4, tag.getAvatar());
            statement.setBytes(5, tag.getBanner());
            return statement.executeUpdate();
        } catch (SQLException ex) {
            //JXErrorPane.showDialog(ex);
            System.out.println(ex.getClass().getName() + " - " + ex.getMessage());
            return 0;
        }
    }


    public User getUser(String name) {
        try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM jr_users WHERE user_name=?;")) {
            statement.setString(1, name);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return new User(
                            rs.getInt(1),
                            rs.getString(2),
                            rs.getBytes(3));
                }
            }
        } catch (SQLException ex) {
            //JXErrorPane.showDialog(ex);
            System.out.println(ex.getClass().getName() + " - " + ex.getMessage());
            return null;
        }
        return null;
    }

    public int insertUser(User user) {
        try (PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO jr_users(user_id, user_name, user_avatar) VALUES(?, ?, ?)")) {
            statement.setInt(1, user.getId());
            statement.setString(2, user.getName());
            statement.setBytes(3, user.getAvatar());
            return statement.executeUpdate();
        } catch (SQLException ex) {
            //JXErrorPane.showDialog(ex);
            System.out.println(ex.getClass().getName() + " - " + ex.getMessage());
            return 0;
        }
    }


    public List<Tag> getPostTags(int postId) {
        List<Tag> tags = new ArrayList<>();
        String sql = "SELECT * FROM jr_tags INNER JOIN jr_post_tags ON jr_tags.tag_id=jr_post_tags.tag_id WHERE jr_post_tags.post_id=?;";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, postId);
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    tags.add(new Tag(
                            rs.getInt("tag_id"),
                            rs.getString("tag"),
                            rs.getString("ref"),
                            rs.getString("ids")));
                }
            }
        } catch (SQLException ex) {
            //JXErrorPane.showFrame(ex);
            System.out.println(ex.getClass().getName() + " - " + ex.getMessage());
            return tags;
        }
        return tags;
    }

    public void setPostTags(int postId, List<Tag> tags) {
        try (PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO jr_post_tags(post_id, tag_id) VALUES(?, ?)")) {
            tags.forEach((tag) -> {
                try {
                    statement.setInt(1, postId);
                    statement.setInt(2, tag.getId());
                    statement.executeUpdate();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });
        } catch (SQLException ex) {
            //JXErrorPane.showDialog(ex);
            System.out.println(ex.getClass().getName() + " - " + ex.getMessage());
            //return null;
        }
    }


    public List<Image> getPostImages(int postId) {
        List<Image> images = new ArrayList<>();
        String sql = "SELECT * FROM jr_post_images WHERE jr_post_images.post_id=?;";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, postId);
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    images.add(new Image(rs.getInt("width"),
                            rs.getInt("height"),
                            rs.getString("ref")));
                }
            }
        } catch (SQLException e) {
            //JXErrorPane.showFrame(e);
            System.out.println(e.getClass().getName() + " - " + e.getMessage());
            return images;
        }
        return images;
    }

    public void setPostImages(int postId, List<Image> images) {
        try (PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO jr_post_images(post_id, width, height, ref) VALUES(?, ?, ?, ?)")) {
            images.forEach((image) -> {
                try {
                    statement.setInt(1, postId);
                    statement.setInt(2, image.getWidth());
                    statement.setInt(3, image.getHeight());
                    statement.setString(4, image.getRef());
                    statement.executeUpdate();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });
        } catch (SQLException ex) {
            //JXErrorPane.showDialog(ex);
            System.out.println(ex.getClass().getName() + " - " + ex.getMessage());
            //return null;
        }
    }


    public Post getPost(int postId) {
        String sql = "SELECT * FROM jr_posts WHERE post_id=?;";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, postId);
            Post post = recordToPost(statement);
            if (post != null) return post;
        } catch (SQLException e) {
            ErrorInfo ii = new ErrorInfo("getPost( " + postId + " )", e.getMessage(), null, null, e, null, null);
            //JXErrorPane.showDialog(null, ii);
            System.out.println(e.getClass().getName() + " - " + e.getMessage());
            return null;
        }
        return null;
    }

    public Post getPrevLatestPost(ZonedDateTime sinse) {
        return getPrevLatestPost(sinse, 1, new ArrayList<>());
    }

    public Post getPrevLatestPost(ZonedDateTime sinse, int delta, List<Tag> tags) {
        String sql = "SELECT * FROM jr_posts WHERE published>? ORDER BY published ASC LIMIT " + delta + ";";
        if (!tags.isEmpty()) {
            sql = "SELECT * FROM jr_posts AS p INNER JOIN jr_post_tags AS pt ON p.post_id=pt.post_id " +
                    "WHERE published>? AND pt.tag_id in (" +
                    StringUtils.join(tags.stream().map(t -> "?").collect(Collectors.toList()), ",") +
                    ") ORDER BY published DESC LIMIT " + delta + ";";
        }
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, sinse.toString());
            if (!tags.isEmpty()) {
                for (int i = 0; i < tags.size(); i++) {
                    statement.setInt(2 + i, tags.get(i).getId());
                }
            }
            Post post = recordToPost(statement);
            if (post != null) return post;
        } catch (SQLException e) {
            //JXErrorPane.showDialog(e);
            System.out.println(e.getClass().getName() + " - " + e.getMessage());
            return null;
        }
        return null;
    }

    public Post getLatestPost(ZonedDateTime sinse) {
        return getLatestPost(sinse, 1, new ArrayList<>());
    }

    public Post getLatestPost(ZonedDateTime sinse, int delta, List<Tag> tags) {
        String sql = "SELECT * FROM jr_posts WHERE published<? ORDER BY published DESC LIMIT " + delta + ";";
        if (!tags.isEmpty()) {
            sql = "SELECT * FROM jr_posts AS p INNER JOIN jr_post_tags AS pt ON p.post_id=pt.post_id " +
                    "WHERE published<? AND pt.tag_id in (" +
                    StringUtils.join(tags.stream().map(t -> "?").collect(Collectors.toList()), ",") +
                    ") ORDER BY published DESC LIMIT " + delta + ";";
        }
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, sinse.toString());
            if (!tags.isEmpty()) {
                for (int i = 0; i < tags.size(); i++) {
                    statement.setInt(2 + i, tags.get(i).getId());
                }
            }
            Post post = recordToPost(statement);
            if (post != null) return post;
        } catch (SQLException e) {
            //JXErrorPane.showDialog(e);
            System.out.println(e.getClass().getName() + " - " + e.getMessage());
            return null;
        }
        return null;
    }

    private Post recordToPost(PreparedStatement statement) throws SQLException {
        Post post = new Post();
        try (ResultSet rs = statement.executeQuery()) {
            while (rs.next()) {
                post.setId(rs.getInt(1));
                post.setUser(getUser(rs.getString(2)));
                post.setPublished(ZonedDateTime.parse(rs.getString(3), DateTimeFormatter.ISO_OFFSET_DATE_TIME));
                post.setComments(rs.getInt(4));
                post.setRating(rs.getBigDecimal(5));
            }
        }
        return post;
    }

    public int insertPost(Post post) {
        try (PreparedStatement statement = connection.prepareStatement("INSERT INTO jr_posts(" +
                "post_id, user_id, published, comments, rating) VALUES(?, ?, ?, ?, ?)")) {
            statement.setInt(1, post.getId());
            statement.setString(2, post.getUser().getName());
            statement.setString(3, post.getPublished().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
            statement.setInt(4, post.getComments());
            statement.setBigDecimal(5, post.getRating());
            return statement.executeUpdate();
        } catch (SQLException ex) {
            ErrorInfo ii = new ErrorInfo("getPost( " + post.getId() + " )", ex.getMessage(), null, null, ex, null, null);
            //JXErrorPane.showDialog(null, ii);
            System.out.println(ex.getClass().getName() + " - " + ex.getMessage());
        }
        return 0;
    }

    public int updatePost(Post post) {
        try (PreparedStatement statement = connection.prepareStatement("UPDATE jr_posts " +
                "SET comments=?, rating=? WHERE post_id=?")) {
            statement.setInt(1, post.getComments());
            statement.setBigDecimal(2, post.getRating());
            statement.setInt(3, post.getId());
            return statement.executeUpdate();
        } catch (SQLException ex) {
            //JXErrorPane.showDialog(ex);
            System.out.println(ex.getClass().getName() + " - " + ex.getMessage());
        }
        return 0;
    }

    public Map<String, BigDecimal> getThisMonthTags() {
        Map<String, BigDecimal> tags = new TreeMap<>();
        String sql = "SELECT * FROM q_this_month_tags;";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    tags.put(rs.getString(1), rs.getBigDecimal(2));
                }
            }
        } catch (SQLException e) {
            //JXErrorPane.showFrame(e);
            System.out.println(e.getClass().getName() + " - " + e.getMessage());
            return tags;
        }
        return tags;
    }

    public Map<String, BigDecimal> getLastDayTags() {
        Map<String, BigDecimal> tags = new TreeMap<>();
        String sql = "SELECT * FROM q_last_day_tags;";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    tags.put(rs.getString(1), rs.getBigDecimal(2).setScale(1));
                }
            }
        } catch (SQLException e) {
            //JXErrorPane.showFrame(e);
            System.out.println(e.getClass().getName() + " - " + e.getMessage());
            return tags;
        }
        return tags;
    }

    public Map<String, BigDecimal> getLastWeekTags() {
        Map<String, BigDecimal> tags = new TreeMap<>();
        String sql = "SELECT * FROM q_last_week_tags;";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    tags.put(rs.getString(1), rs.getBigDecimal(2).setScale(1));
                }
            }
        } catch (SQLException e) {
            //JXErrorPane.showFrame(e);
            System.out.println(e.getClass().getName() + " - " + e.getMessage());
            return tags;
        }
        return tags;
    }

    public Map<String, BigDecimal> getLastMonthTags() {
        Map<String, BigDecimal> tags = new TreeMap<>();
        String sql = "SELECT * FROM q_last_month_tags;";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    tags.put(rs.getString(1), rs.getBigDecimal(2).setScale(1));
                }
            }
        } catch (SQLException e) {
            //JXErrorPane.showFrame(e);
            System.out.println(e.getClass().getName() + " - " + e.getMessage());
            return tags;
        }
        return tags;
    }

    public Map<String, BigDecimal> getLastYearTags() {
        Map<String, BigDecimal> tags = new TreeMap<>();
        String sql = "SELECT * FROM q_last_year_tags;";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    tags.put(rs.getString(1), rs.getBigDecimal(2).setScale(1));
                }
            }
        } catch (SQLException e) {
            //JXErrorPane.showFrame(e);
            System.out.println(e.getClass().getName() + " - " + e.getMessage());
            return tags;
        }
        return tags;
    }
}
