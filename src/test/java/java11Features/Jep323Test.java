package java11Features;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;

import es.vass.java11.features.IMath;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Jep323Test {
	
	private static final Logger log = LogManager.getLogger(Jep323Test.class);

	@Test
	public void testSum() {
		IMath sum = (var s1, var s2) -> s1 + s2;
		var am = sum.doMath(1, 2);
		assertEquals(3, am);
	}

	@Test
	public void testSubstract() {
		IMath sum = (var s1, var s2) -> s1 - s2;

		assertEquals(1, sum.doMath(2, 1));
	}

	@Test
	public void testMultiply() {
		IMath prod = (var s1, var s2) -> s1 * s2;

		assertEquals(3, prod.doMath(1, 2));
	}

	
	static class Client {
		private int x;

		public Client(int x) {
			log.info("new Instance: {}", x);
			this.x = x;
		}

		CompletableFuture<Integer> runAsync() {
			return CompletableFuture.supplyAsync(() -> longProcess()).orTimeout(60, TimeUnit.SECONDS)
					.handle((response, ex) -> {
						if (!Objects.isNull(ex)) {
							log.error(ex.getLocalizedMessage(), ex);
						}
						return response;
					});
		}

		private Integer longProcess() {
			log.info("Running: {}", this.x);
			sleep(2);
			return 0;
		}

//		@SneakyThrows
		private void sleep(int seconds) {
			try {
				Thread.sleep(seconds * 1000);
			} catch (Exception e) {
				// TODO: handle exception
			}
			
		}
	}
	
	Supplier<Integer> process = () -> {
		var cores = Runtime.getRuntime().availableProcessors();
		var clients = IntStream.rangeClosed(1, (cores - 1) * 2).boxed().map(Client::new).collect(Collectors.toList());
		var futureRequests = clients.stream().map(Client::runAsync).collect(Collectors.toList());
		futureRequests.stream().map(CompletableFuture::join).collect(Collectors.toList());
		return 0;
	};


}
