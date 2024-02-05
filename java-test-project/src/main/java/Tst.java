import java.util.UUID;

public class Tst {
    public static void main(String[] args) {
        UUID uuid = UUID.randomUUID(); // 生成随机的UUID
        String uuidStr = uuid.toString().replace("-", "");
        System.out.println("Generated UUID: " + uuidStr);
    }
}
