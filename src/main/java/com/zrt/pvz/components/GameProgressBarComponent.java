package com.zrt.pvz.components;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.texture.Texture;
import com.zrt.pvz.data.LevelData;
import com.zrt.pvz.data.PlantData;
import com.zrt.pvz.data.ZombieData;
import javafx.animation.ScaleTransition;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Point2D;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.util.*;

import static com.almasb.fxgl.dsl.FXGL.spawn;

/**
 * @author 曾瑞庭
 * @Description:
 * @date 2023/12/18 19:10
 */
public class GameProgressBarComponent extends Component {
    private LevelData levelData;
    private Map<String, Integer> zombieMap;
    private Map<String,ZombieData>zombieDataMap;
    private Random random=new Random();
    private double width;
    private double totalTime;
    private static final List<Point2D> bornPoints= Arrays.asList(
            new Point2D(900, 80),
            new Point2D(900, 165),
            new Point2D(900, 250),
            new Point2D(900, 335),
            new Point2D(900, 420)
    ); //僵尸出生点
    private double progressNow;
    private List<Double> waves;
    private List<Integer> waveZombieNumber;
    private boolean[] reachWaveFlags;
    private SimpleDoubleProperty progress =new SimpleDoubleProperty();
    @Override
    public void onAdded(){
        //初始化
        levelData=entity.getObject("levelData");
        waves=levelData.waves();
        zombieDataMap=new HashMap<>();
        zombieMap=levelData.zombieMap();
        waveZombieNumber=levelData.waveZombieNumber();
        totalTime=waves.get(0);
        reachWaveFlags=new boolean[waves.size()];
        //显示进度条有关图片组成
        Texture progressBar=FXGL.texture("ui/progressBar/FlagMeterFull.png");
        width=progressBar.getWidth();
        Texture progressBarBG=FXGL.texture("ui/progressBar/FlagMeterEmpty.png");
        Texture levelProgress=FXGL.texture("ui/progressBar/FlagMeterLevelProgress.png");
        Texture zombieHead=FXGL.texture("ui/progressBar/FlagMeterParts1.png");
        Texture[] waveFlags=new Texture[waves.size()];
        StackPane pane=new StackPane(progressBarBG,progressBar);
        levelProgress.setLayoutY(12);
        levelProgress.setLayoutX(40);
        zombieHead.setLayoutY(-2);
        //设置遮造层，即遮挡后却露出那部分图片
        Rectangle rectangle=new Rectangle(progressBar.getWidth(),progressBar.getHeight());
        rectangle.setTranslateX(width);
        progressBar.setClip(rectangle);
        //设置关卡信息
        Text text=new Text(levelData.name());
        Font font=Font.loadFont(getClass().getResource("/fonts/fzjz.ttf").toExternalForm(), 20);
        text.setFont(font);
        text.setFill(Color.web("#D6B35E"));
        text.setTranslateX(-80);
        text.setLayoutY(18);
        entity.getViewComponent().addChild(text);

        entity.getViewComponent().addChild(pane);
        entity.getViewComponent().addChild(levelProgress);
        entity.getViewComponent().addChild(zombieHead);

        //按位置间隔生成旗子
        for(int i=0;i<waves.size();i++) {
            waveFlags[i]=FXGL.texture("ui/progressBar/FlagMeterParts2.png");
            waveFlags[i].setLayoutX(width*(1.0-waves.get(i)/totalTime));
            entity.getViewComponent().addChild(waveFlags[i]);
        }
        //记录时间
        progress.addListener((ob,ov,nv)->{
            progressNow=Math.min(1.0,nv.doubleValue()/totalTime);
            rectangle.setTranslateX(width-progressNow*width);
            zombieHead.setLayoutX(width-progressNow*width-10);
            for(int i=0;i<waves.size();i++){
                if(progressNow>=waves.get(i)/totalTime){
                    waveFlags[i].setLayoutY(Math.max(-1,(1-nv.doubleValue()/waves.get(i))*10)*10);
                    if(!reachWaveFlags[i]) {
                        reachWaveFlags[i]=true;
                        for(int j=0;j<5;j++)//确保每一路都有
                            spawnEnemy(j,0);
                        for(int j=0;j<waveZombieNumber.get(i);j++)//随机生成多个
                            spawnEnemy(random.nextInt(5),random.nextInt(20));
                        showWarning();
                    }
                }
            }
        });
    }
    @Override
    public void onUpdate(double tpf){
        progress.set(progress.get()+tpf);
    }

    //显示一大波僵尸正在来袭与最后一波
    public void showWarning(){
        Texture texture1 = FXGL.texture("ui/progressBar/LargeWave.png");
        Duration duration=Duration.seconds(2);
        ScaleTransition st1=new ScaleTransition(duration,texture1);
        st1.setFromX(2);
        st1.setToX(1);
        st1.setFromY(2);
        st1.setToY(1);
        texture1.setLayoutY(-270);
        texture1.setLayoutX(-300);
        entity.getViewComponent().addChild(texture1);
        st1.play();
        st1.setOnFinished(event->{
            entity.getViewComponent().removeChild(texture1);
            if(reachWaveFlags[0]){
                Texture texture2 = FXGL.texture("ui/progressBar/FinalWave.png");
                Duration duration2=Duration.seconds(2);
                ScaleTransition st2=new ScaleTransition(duration2,texture2);
                st2.setFromX(2);
                st2.setToX(1);
                st2.setFromY(2);
                st2.setToY(1);
                texture2.setLayoutY(-270);
                texture2.setLayoutX(-300);
                entity.getViewComponent().addChild(texture2);
                st2.play();
                st2.setOnFinished(event1->{
                    entity.getViewComponent().removeChild(texture2);
                });
            }
        });
    }

    //生成僵尸
    public void spawnEnemy(int tmp,int offset) {
        //初始化数据Map
        for (String key : zombieMap.keySet()) {
            zombieDataMap.put(key,FXGL.getAssetLoader().loadJSON("data/zombie/"+key+".json", ZombieData.class).get());
        }
        //总权重
        int totalWeight = zombieMap.values().stream().mapToInt(Integer::intValue).sum();
        int randomNumber = random.nextInt(totalWeight) + 1; // 在总权重范围内生成随机数
        ZombieData zombieData=null;
        int cumulativeWeight = 0;
        for (Map.Entry<String, Integer> entry : zombieMap.entrySet()) {
            cumulativeWeight += entry.getValue();
            if (randomNumber <= cumulativeWeight) {
                zombieData=zombieDataMap.get(entry.getKey());
                break;
            }
        }
        //随机抽取数组的一个坐标
        Point2D point2Dtmp=new Point2D(bornPoints.get(tmp).getX()+offset, bornPoints.get(tmp).getY());
        //如果可以产生僵尸,那么生成僵尸
        Entity zombie=spawn("zombie",
                new SpawnData(point2Dtmp)
                        .put("zombieData", zombieData)
                        .put("row",tmp));
    }
}

