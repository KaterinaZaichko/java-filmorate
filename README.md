# java-filmorate

![Filmorate](https://user-images.githubusercontent.com/99019116/233731876-c39bb009-bdaf-4370-86d1-9d32f149eb88.png)

Примеры запросов:
* получение всех фильмов:
  ```
  SELECT f.id, 
         f.name, 
         f.description,
         f.release_date,
         f.duration, 
         f.mpa, 
         GROUP_CONCAT(g.id) AS genres 
  FROM films AS f 
  LEFT JOIN m2m_film_genre AS m2mfg ON m2mfg.f_id = f.id 
  LEFT JOIN genres AS g ON g.id = m2mfg.g_id 
  GROUP BY f.id;
  ```
* получение всех пользователей:
  ```
  SELECT id, 
         email, 
         login, 
         name, 
         birthday 
  FROM users 
  ORDER BY id;
  ```
* получение списка наиболее популярных фильмов:
  ```
  SELECT f.id, 
         f.name, 
         f.description, 
         f.release_date, 
         f.duration, 
         f.mpa, 
         GROUP_CONCAT(g.id) AS genres, 
         COUNT(m2ml.f_id) AS popularity
  FROM films AS f 
  LEFT JOIN m2m_film_genre AS m2mfg ON m2mfg.f_id = f.id 
  LEFT JOIN genres AS g ON g.id = m2mfg.g_id 
  LEFT JOIN m2m_likes AS m2ml ON f.id = m2ml.f_id 
  GROUP BY f.id 
  ORDER BY f.id DESC 
  LIMIT <count>;
  ```
* получение списка общих друзей с другим пользователем:
  ```
  SELECT u.id, 
         u.email, 
         u.login, 
         u.name, 
         u.birthday 
  FROM (SELECT m2mf.friend_id AS friends 
  FROM users AS u 
  JOIN m2m_friends AS m2mf ON u.id = m2mf.u_id
  WHERE u.id IN (<firstFriendID>, <secondFriendId>)
  GROUP by m2mf.friend_id
  HAVING COUNT(m2mf.friend_id) > 1) AS t
  LEFT JOIN users AS u ON t.friends = u.id 
  GROUP BY u.id;
  ```
