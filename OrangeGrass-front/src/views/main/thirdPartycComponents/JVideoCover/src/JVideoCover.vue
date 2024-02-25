<template>
    <div :id="uid + '-j-video-cover'" class="j-video-cover">
        <video
            :id="uid + '-video'"
            class="j-video"
            :src="videoUrl"
            controls="controls"
            crossorigin="anonymous"
            playsinline
            autoplay
            muted
            :loop="loop"
        ></video>
        <div
            v-if="coverLongImg"
            :id="uid + '-cover-long-img-box'"
            class="cover-long-img-box"
            @mousemove="imgHover"
            @mouseleave="hoverOut"
            @click="coverClick"
        >
            <img
                alt=""
                :src="coverLongImg"
                class="cover-long-img"
                :id="uid + '-cover-long-img'"
            />
        </div>
        <img
            v-else
            alt=""
            :id="uid + '-coverImg'"
            :src="coverSrc"
            class="j-coverImg"
            @mousemove="imgHover"
            @mouseleave="hoverOut"
            @click="coverClick"
        />
        <progress
            v-if="imgList.length > 1 || coverLongImg"
            :id="uid + '-progress'"
            class="j-progress-bo j-progress"
            :value="pauseTime"
            :max="duration"
            :style="'width:' + width"
        ></progress>
        <div class="video-info" :id="uid + '-video-info'">
            <slot name="video-info-slot"></slot>
            <span v-if="showDuration" class="video-duration">{{
                calcTime(duration)
            }}</span>
        </div>
        <progress
            v-if="imgList.length > 1 || coverLongImg"
            :id="uid + '-progress1'"
            class="j-progress-to j-progress"
            :value="progressValue"
            :max="stepNums"
            :style="'width:' + width"
        ></progress>
    </div>
</template>

<script>
import {getUId} from "../../../../../utils/strTool";

export default {
    name: "JVideoCover",
    props: {
        width: {
            type: String,
            default: "300px",
        },
        height: {
            type: String,
            default: "200px",
        },
        stepNums: {
            type: String,
            default: "30",
        },
        videoUrl: {
            type: String,
            default: "",
        },
        loop: {
            type: Boolean,
            default: false,
        },
        showDuration: {
            type: Boolean,
            default: true,
        },
        coverLongImg: {
            type: String,
            default: "",
        },
    },
    data() {
        return {
            uid: "",
            video: "",
            imgList: [],
            currentTime: 0,
            duration: 0,
            pauseTime: 0,
            coverSrc: "",
            pauseCover: "",
            progressValue: 0,
        };
    },
    computed: {},
    created() {
        this.setUid();
    },
    mounted() {
        this.init();
    },
    methods: {
        setUid() {
            this.uid = getUId();
        },
        calcTime(t) {
          // 小时： h = parseInt(总秒数 / 60 / 60 % 24)
          // 分钟： m = parseInt(总秒数 / 60 % 60)
          // 秒：   s = parseInt(总秒数 % 60)

          let h = parseInt(t / 60 / 60 % 24)
          let m = parseInt(t / 60 % 60)
          let s = parseInt(t % 60 )
          // 因为h已经是数字型了，如果0不加引号就变成加法了
          h = h < 10 ? '0' + h : h
          m = m < 10 ? '0' + m : m
          s = s < 10 ? '0' + s : s
          return h+ '小时' + m + '分' + s + '秒'
        },
        init() {
            const videoContentShow = document.getElementById(
                this.uid + "-video"
            );
            const coverLongImg = document.getElementById(
                this.uid + "-cover-long-img"
            );
            const coverLongImgBox = document.getElementById(
                this.uid + "-cover-long-img-box"
            );
            const coverImg = document.getElementById(this.uid + "-coverImg");
            videoContentShow.style.height = this.height;
            videoContentShow.style.width = this.width;
            const videoContent = videoContentShow.cloneNode();
            videoContent.addEventListener("canplay", () => {
                this.duration = videoContent.duration;
                if (!this.coverLongImg && this.currentTime < this.duration)
                    this.cut(videoContent);
                else this.progressValue = 0;
            });
            if (!this.coverLongImg) {
                coverImg.style.height = this.height;
                coverImg.style.width = this.width;
            } else {
                coverLongImg.style.height = this.height;
                coverLongImgBox.style.height = this.height;
                coverLongImgBox.style.width = this.width;
            }
            videoContentShow.addEventListener("pause", () => {
                this.pauseTime = videoContentShow.currentTime;
                this.pauseCover = this.cutCover(
                    videoContentShow,
                    videoContentShow.currentTime
                );
                coverImg && coverImg.setAttribute("src", this.pauseCover);
                coverLongImg &&
                    coverLongImg.setAttribute("src", this.pauseCover);
                const step = this.duration / this.stepNums;
                const index = Math.ceil(this.pauseTime / step);
                this.progressValue = index;
                setTimeout(() => {
                    if (videoContentShow.paused) this.doHide();
                }, 200);
            });
        },
        coverImgChang(e, coverImg) {
            const w = coverImg.offsetWidth / this.stepNums;
            const x = e.offsetX - coverImg.offsetLeft;
            const index = Math.min(
                Math.max(Math.ceil(x / w), 1),
                this.stepNums
            );
            if (this.imgList.length < index) return;
            this.progressValue = index;
            coverImg.setAttribute(
                "src",
                this.imgList[Math.min(this.imgList.length - 1, index)]
            );
        },
        coverLongImgChang(e, coverLongImg) {
            const coverLongImgBox = document.getElementById(
                this.uid + "-cover-long-img-box"
            );
            if (coverLongImg.src != this.coverLongImg) {
                coverLongImg.setAttribute("src", this.coverLongImg);
            }
            const w = coverLongImgBox.offsetWidth / this.stepNums;
            const x = e.offsetX - Math.abs(coverLongImg.offsetLeft);
            const index = Math.min(
                Math.max(Math.ceil(x / w), 1),
                this.stepNums
            );
            this.progressValue = index;
            coverLongImg.style.right =
                (index - 1) * (coverLongImg.offsetWidth / this.stepNums) + "px";
        },
        imgHover(e) {
            const coverImg = document.getElementById(this.uid + "-coverImg");
            coverImg && this.coverImgChang(e, coverImg);
            const coverLongImg = document.getElementById(
                this.uid + "-cover-long-img"
            );
            coverLongImg && this.coverLongImgChang(e, coverLongImg);
        },
        hoverOut() {
            const coverImg = document.getElementById(this.uid + "-coverImg");
            const coverLongImg = document.getElementById(
                this.uid + "-cover-long-img"
            );
            const step = this.duration / this.stepNums;
            const index = Math.ceil(this.pauseTime / step);
            this.progressValue = index;
            if (coverImg) {
                coverImg.setAttribute("src", this.pauseCover || this.coverSrc);
            } else {
                coverLongImg.setAttribute(
                    "src",
                    this.pauseCover || this.coverSrc || this.coverLongImg
                );
                coverLongImg.style.right = 0;
            }
        },
        doHide(hide = false) {
            const videoContent = document.getElementById(this.uid + "-video");
            videoContent.style.display = hide ? "block" : "none";
            videoContent.currentTime = this.pauseTime;
            hide ? videoContent.play() : videoContent.pause();
            const img = document.getElementById(this.uid + "-coverImg");
            if (img) {
                img.style.display = hide ? "none" : "block";
            } else {
                const coverLongImgBox = document.getElementById(
                    this.uid + "-cover-long-img-box"
                );
                coverLongImgBox.style.display = hide ? "none" : "block";
            }
            const progress = document.getElementById(this.uid + "-progress");
            progress.style.display = hide ? "none" : "block";
            const progress1 = document.getElementById(this.uid + "-progress1");
            progress1.style.display = hide ? "none" : "block";
            const videoInfo = document.getElementById(this.uid + "-video-info");
            videoInfo.style.display = hide ? "none" : "block";
        },
        coverClick() {
            this.doHide(true);
        },
        cutCover(video, currentTime) {
            video.currentTime = currentTime;
            const canvas = document.createElement("canvas")
              // .setAttribute("crossOrigin",'Anonymous');
            let ctx = canvas.getContext("2d");
            canvas.width = parseInt(this.width);
            canvas.height = parseInt(this.height);
            ctx.drawImage(video, 0, 0, canvas.width, canvas.height);
            const img = canvas.toDataURL("image/png");
            return img;
        },
        cut(video) {
            const duration = video.duration;
            this.duration = duration;
            this.currentTime += duration / this.stepNums;
            const img = this.cutCover(video, this.currentTime);
            this.imgList.push(img);
            if (this.imgList.length == 2) {
                this.coverSrc = img;
                const coverImg = document.getElementById(
                    this.uid + "-coverImg"
                );
                coverImg.setAttribute("src", img);
            }
        },
    },
};
</script>

<style lang="less" scoped>
.j-video-cover {
    position: relative;
}
.j-video {
    display: none;
}
progress {
    height: 8px;
}
.j-progress-bo {
    position: absolute;
    left: 0;
    bottom: 0;
}
.j-progress-to {
    position: absolute;
    left: 0;
    top: 0;
}
.j-progress1::-webkit-progress-bar {
    background: #4c4c4c;
}
.j-progress1::-webkit-progress-value {
    background: #a21211;
    border-radius: 0.2rem;
}
.j-progress1::progress-bar {
    background: #4c4c4c;
}
.j-progress1::progress-value {
    background: #a21211;
    border-radius: 0.2rem;
}
.video-info {
    position: absolute;
    left: 0;
    bottom: 15px;
    width: 100%;
    .video-duration {
        float: right;
        margin-right: 1em;
    }
}
.cover-long-img-box {
    overflow: hidden;
    .cover-long-img {
        position: relative;
    }
}
</style>
