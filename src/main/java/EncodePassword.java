import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class EncodePassword {

	public static void main(String[] args) {
		if (args.length != 1) {
			System.err.println("EncodePassword requires single argument <plaintext-password>, and outputs encoded password!");
		}

		PasswordEncoder encoder = new BCryptPasswordEncoder(11);
		System.out.println(encoder.encode(args[0]));
	}

}
