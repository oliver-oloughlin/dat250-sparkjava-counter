package no.hvl.dat250.rest.todos;

import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.List;
import static spark.Spark.*;

/**
 * Rest-Endpoint.
 */
public class TodoAPI {

    static List<Todo> todos = new ArrayList<Todo>();
    static Gson gson = new Gson();

    public static void main(String[] args) {
        if (args.length > 0) {
            port(Integer.parseInt(args[0]));
        } else {
            port(8080);
        }

        after((req, res) -> res.type("application/json"));

        get("/todos", (req, res) -> gson.toJson(todos));

        get("/todos/:id", (req, res) -> {
            String idStr = req.params("id");
            try {
                long id = Long.parseLong(idStr);
                Todo todo = null;
                for (Todo t : todos) {
                    if (t.getId() == id) {
                        todo = t;
                        break;
                    }
                }

                if (todo == null) {
                    res.status(404);
                    res.body("Todo with the id  \"" + id + "\" not found!");
                } else {
                    res.status(200);
                    res.body(gson.toJson(todo));
                }
                return res;
            } catch (NumberFormatException e) {
                res.status(400);
                res.body("The id \"" + idStr + "\" is not a number!");
                return res;
            }
        });

        post("/todos", (req, res) -> {
            try {
                Todo todoData = gson.fromJson(req.body(), Todo.class);
                Todo todo = new Todo((long)(Math.random() * 1_000_000_000), todoData.getSummary(), todoData.getDescription());
                todos.add(todo);
                res.status(201);
                res.body(gson.toJson(todo));
                return res;
            } catch (Exception e) {
                System.out.println(e.getStackTrace());
                res.status(400);
                return res;
            }
        });

        put("/todos/:id", (req, res) -> {
            String idStr = req.params("id");
            try {
                long id = Long.parseLong(idStr);
                Todo todoData = gson.fromJson(req.body(), Todo.class);
                Todo todo = new Todo(id, todoData.getSummary(), todoData.getDescription());
                boolean removed = todos.removeIf(t -> t.getId() == id);
                if (removed) {
                    todos.add(todo);
                    res.status(200);
                    res.body(gson.toJson(todo));
                } else {
                    res.status(404);
                }
                return res;
            } catch (NumberFormatException e) {
                res.status(400);
                res.body("The id \"" + idStr + "\" is not a number!");
                return res;
            } catch (Exception e) {
                res.status(400);
                return res;
            }
        });

        delete("/todos/:id", (req, res) -> {
            String idStr = req.params("id");
            try {
                long id = Long.parseLong(idStr);
                Todo todo = null;
                for (Todo t: todos) {
                    if (t.getId() == id) {
                        todo = t;
                        break;
                    }
                }

                if (todo == null) {
                    res.status(404);
                } else {
                    todos.remove(todo);
                    res.status(200);
                    res.body(gson.toJson(todo));
                }
                return res;
            } catch (NumberFormatException e) {
            res.status(400);
            res.body("The id \"" + idStr + "\" is not a number!");
            return res;
        } catch (Exception e) {
            res.status(400);
            return res;
        }
        });
    }
}
