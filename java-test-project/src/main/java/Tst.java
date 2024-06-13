import java.util.UUID;

public class Tst {
    public static void main(String[] args) {
        UUID uuid = UUID.randomUUID(); // 生成随机的UUID
        String uuidStr = uuid.toString().replace("-", "");
        System.out.println("Generated UUID: " + uuidStr);

        String s = "workflow.argoproj.io/hello-worldfwc27 created";
        System.out.println(s.split(" ")[0].split("/")[1]);
        StringBuilder sb = new StringBuilder();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        String st = "Completed";
        System.out.println(st.toLowerCase());

    }
}
