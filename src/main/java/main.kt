import generator.GeneratorService
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*

// TODO 1: Generate map full of walls
// TODO 2: Select random initial position
// TODO 3: Walk random direction for t steps
// TODO 3.1 for every step change direction with p=0.35
// TODO 3.2 every visited position is emptied, along with some surrounding blocks


fun main(args: Array<String>) {

    (0 until 10).forEach {
        val id = UUID.randomUUID().toString()
        val room = GeneratorService.instance.generate(20,20)

        Files.write(
                Paths.get("levels/$id.lvl"),
                room.toByteArray()
        )
    }
}