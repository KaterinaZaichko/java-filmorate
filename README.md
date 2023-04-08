# java-filmorate

![](../../../Users/kater/OneDrive/Рабочий стол/Filmorate.png)

Примеры запросов:
* получение всех фильмов:
  ```
  SELECT f.f_id AS id, f.f_name AS name, f.f_description AS description, f.f_release_date AS release date, f.f_duration AS duration, GROUP_CONCAT(g.g_name) AS genre, r.r_name AS rating
  FROM film AS f
  JOIN rating AS r ON r.r_id = f.f_rating
  JOIN m2m_film_genre AS m2mfg ON m2mfg.f_id = f.f_id
  JOIN genre AS g ON g.g_id = m2mfg.g_id
  GROUP BY f.f_id;
  ```
* получение всех пользователей:
  ```
  SELECT t.email, t.login, t.name, t.birthday, GROUP_CONCAT(u.u_name) AS friends
  FROM (SELECT u.u_email AS email, u.u_login AS login, u.u_name AS name, u.u_birthday AS birthday, m2mfl.friend_id AS friends
  FROM user AS u
  JOIN m2m_friends_list AS m2mfl ON u.u_id = m2mfl.u_id
  WHERE m2mfl.status = 1) AS t
  LEFT JOIN user AS u ON t.friends = u.u_id
  group by u.u_name;
  ```
* получение списка наиболее популярных фильмов:
  ```
  SELECT f.f_name AS name, COUNT(m2mll.f_id) AS popularity
  FROM film AS f
  JOIN m2m_likes_list AS m2mll ON f.f_id = m2mll.f_id
  GROUP BY m2mll.f_id
  ORDER BY m2mll.f_id DESC
  LIMIT 10;
  ```
* получение списка общих друзей с другим пользователем:
  ```
  SELECT u.u_name AS friends
  FROM (SELECT m2mfl.friend_id AS friends
  FROM user AS u
  JOIN m2m_friends_list AS m2mfl ON u.u_id = m2mfl.u_id
  WHERE u.u_id IN (1, 2) AND m2mfl.status = true
  GROUP by m2mfl.friend_id
  HAVING COUNT(m2mfl.friend_id) > 1) AS t
  LEFT JOIN user AS u ON t.friends = u.u_id
  GROUP BY u.u_name;
  ```