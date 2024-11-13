import java.util.Arrays;
import java.util.Objects;
import java.util.Random;

public class Lesson_4_1 {
    public static Random rand = new Random();
    public static int bossHealth = 600;
    public static int bossDamage = 50;
    public static String bossDefence;
    public static int[] heroesHealth = {270, 260, 250, 250};
    public static int[] heroesDamage = {20, 15, 10, 0}; // У медика 0 дамаг, урон не наносит
    public static String[] heroesAttackType =
            {"Physical", "Magical", "Kinetic",
                    "Medic"};
    // Шаг №1 Добавили медика
    public static int roundNumber;

    public static void main(String[] args) {
        printStatistics(new int[]{0, 0});

        while (!isGameOver()) {
            playRound();
        }
    }

    public static boolean isGameOver() {
        if (bossHealth <= 0) {
            System.out.println("Heroes won!!!");
            return true;
        }
        boolean allHeroesDead = true;
        for (int i : heroesHealth) {
            if (i > 0) {
                allHeroesDead = false;
                break;
            }
        }
        if (allHeroesDead) {
            System.out.println("Boss won!!!");
            return true;
        }
        return false;
    }

    public static void chooseBossDefence() {
        Random random = new Random();
        if (roundNumber != 0) {
            int randomIndex = random.nextInt(heroesAttackType.length);
            bossDefence = heroesAttackType[randomIndex];
        }

    }

    public static void playRound() {
        roundNumber++;
        chooseBossDefence();
        bossAttack();
        int[] totalDamage = heroesAttack();
        printStatistics(totalDamage);
        healthUpMedic(); // Медик лечит после каждого раунда
    }


    public static void bossAttack() {
        for (int i = 0; i < heroesHealth.length; i++) {
            if (heroesHealth[i] > 0) {
                heroesHealth[i] = Math.max(heroesHealth[i] - bossDamage, 0);
            }
        }
    }

    public static int[] heroesAttack() {
        int totalDamage = 0;
        int critDamage = 0;

        for (int i = 0; i < heroesDamage.length; i++) {
            if (heroesHealth[i] > 0 && bossHealth > 0 && heroesDamage[i] > 0) {
                int damage = heroesDamage[i];
                if (Objects.equals(heroesAttackType[i], bossDefence)) {
                    int ex = rand.nextInt(9) + 2;
                    damage *= ex;
                    critDamage += damage;
                }
                totalDamage += damage;
                bossHealth = Math.max(bossHealth - damage, 0);
            }
        }

        return new int[]{totalDamage, critDamage};
    }


    // Шаг №1 Medic
    public static void healthUpMedic() {
        int medicIndex = Arrays.asList(heroesAttackType).indexOf("Medic");

        for (int i = 0; i < heroesHealth.length; i++) {
            if (i != medicIndex // Сам себя не лечит
                    && heroesHealth[medicIndex] > 0 // Лечит если только сам жив
                    && heroesHealth[i] < 100 // Лечит если меньше 100 жизней
                    && heroesHealth[i] > 0   // Но не может оживить
            ) {

                int health = rand.nextInt(80) + 20; // Медик лечит на рандомное количество единиц
                heroesHealth[i] += health;
                System.out.println("--> " + heroesAttackType[i] + " was added " + health + " health!");
                break; // Может лечить только одного члена команды
            }

        }
    }

    public static void printStatistics(int[] damage) {
        System.out.println("\n----------- ROUND №" + roundNumber + " ---------------");

        System.out.println("BOSS health: " + bossHealth + ", damage: " + bossDamage
                + ", defence: " + (bossDefence == null ? "No defence" : bossDefence));

        for (int i = 0; i < heroesAttackType.length; i++) {
            System.out.println(heroesAttackType[i] +
                    " health: " + (heroesHealth[i] == 0 ? "is died" : heroesHealth[i]) + ", damage: " + heroesDamage[i]);
        }

        System.out.println("--> Total damage: " + damage[0] + ", of them crit damage: " + damage[1]);

        System.out.println("-- END Stats\n");
    }
}
