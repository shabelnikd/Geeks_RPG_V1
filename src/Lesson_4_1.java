import java.util.Arrays;
import java.util.Objects;
import java.util.Random;

public class Lesson_4_1 {
    public static Random rand = new Random();
    public static int bossHealth = 1000;
    public static int bossDamage = 50;
    public static String bossDefence;
    public static boolean bossIsStun = false;
    public static int[] heroesHealth = {270, 260, 250, 250, 270, 250, 500, 300};
    public static int[] heroesDamage = {20, 15, 10, 0, 15, 0, 5, 0}; // У медика, ведьмака и тора 0 дамаг, урон не наносят, а у Голема слабый удар
    public static String[] heroesAttackType =
            {"Physical", "Magical", "Kinetic",
                    "Medic", "Lucky", "Thor", "Gollum", "Witcher"};
    // Шаг №1 Добавили медика
    // Шаг №2 Добавили Thor
    // Шаг №3 Добавили Gollum
    // Шаг №4 Добавили Wither
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
        int luckyIndex = Arrays.asList(heroesAttackType).indexOf("Lucky");

        int gollumIndex = Arrays.asList(heroesAttackType).indexOf("Gollum");
        int totalSavedHealth = 0;

        for (int i = 0; i < heroesHealth.length; i++) {
            if (bossIsStun) { // Если босс имет стан от тора, то break и пропуск урона, и стан убирается
                bossIsStun = false;
                break;
            }

            if (heroesHealth[i] > 0) {
                if (i == luckyIndex && rand.nextBoolean()) { // Если Lucky повезло, то итерация пропускается урон не проходит
                    System.out.println("--> Lucky is Lucky on this round");
                    continue;
                }

                if (heroesHealth[gollumIndex] > 0 && i != gollumIndex) {
                    int partDamage = bossDamage / 5; // Голем принимает на себя 1/5 урона от босса
                    heroesHealth[gollumIndex] = Math.max(heroesHealth[gollumIndex] - partDamage, 0);
                    heroesHealth[i] = Math.max(heroesHealth[i] - (bossDamage - partDamage), 0);
                    totalSavedHealth += partDamage;
                    continue;
                } else if (i == gollumIndex) {
                    continue; // Не было указано что голем получает урон от босса, поэтому я решил без урона, только 1/5
                }

                heroesHealth[i] = Math.max(heroesHealth[i] - bossDamage, 0);
            }
        }

        if (totalSavedHealth > 0) {
            System.out.println("--> Total Gollum Saved Health: " + totalSavedHealth);
        }
    }

    public static int[] heroesAttack() {
        int totalDamage = 0;
        int critDamage = 0;

        int thorIndex = Arrays.asList(heroesAttackType).indexOf("Thor");
        int witcherIndex = Arrays.asList(heroesAttackType).indexOf("Witcher");

        for (int i = 0; i < heroesDamage.length; i++) {

            if (i == thorIndex && rand.nextInt(10) < 3 && heroesHealth[thorIndex] > 0) { // С шансом 30% Тор застанит босса
                bossIsStun = true;
                System.out.println("--> Boss is stunned on next round");
            }

            if (i == witcherIndex && heroesHealth[witcherIndex] > 0) { // Если ведьмак жив
                for (int j = 0; j < heroesHealth.length; j++){ // То он смотрит кто умер
                    if (heroesHealth[j] <= 0) {
                        heroesHealth[j] = heroesHealth[witcherIndex]; // И отдает ему свою жизнь
                        heroesHealth[witcherIndex] = 0; // Но сам погибает
                        System.out.println("--> Witcher was saved life of " + heroesAttackType[j] + " and add his " + heroesHealth[j]);
                        break;
                    }
                }
            }

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

        System.out.println("--> Total damage to boss: " + damage[0] + ", of them crit damage: " + damage[1]);

        System.out.println("-- END Stats\n-- Round buffs:");
    }
}
