import generator.GeneratorService
import org.apache.commons.codec.binary.Hex
import org.apache.commons.codec.digest.DigestUtils
import java.nio.file.Files
import java.nio.file.Paths

// TODO 1: Generate map full of walls
// TODO 2: Select random initial position
// TODO 3: Walk random direction for t steps
// TODO 3.1 for every step change direction with p=0.35
// TODO 3.2 every visited position is emptied, along with some surrounding blocks


fun main(args: Array<String>) {

    (0 until 10).toList().parallelStream().forEach {

        println("Starting thread $it")

        (0 until 100000).forEach {
            val room = GeneratorService.instance.generate(20, 20).toByteArray()

            // generate md5 checksum of level
            val id = String(Hex.encodeHex(DigestUtils.getMd5Digest().digest(room)))

            Files.write(Paths.get("../levels/easy/$id.lvl"), room)
        }

        println("Thread $it finished")
    }
}