<template>
  <div class="emoji-content">
    <div class="emoji">
      <div class="emoji-wrapper">
        <ul class="emoji-list">
          <li
            class="emoji-item"
            v-for="(item, index) in emojiList"
            :key="index"
            @click="sendEmoji(item)"
          >
            <img :src="item" alt="" />
          </li>
        </ul>
      </div>
    </div>
    <div class="mask" @click="closeEmoji"></div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      emojiList: [
        require("../../assets/images/emoji/slightly-smiling-face.png"),
        require("../../assets/images/emoji/smiling-face.png"),
        require("../../assets/images/emoji/smiling-face-with-heart-eyes.png"),
        require("../../assets/images/emoji/smiling-face-with-sunglasses.png"),
        require("../../assets/images/emoji/thinking-face.png"),
        require("../../assets/images/emoji/tired-face.png"),
        require("../../assets/images/emoji/money-mouth-face.png"),
        require("../../assets/images/emoji/loudly-crying-face.png"),
        require("../../assets/images/emoji/pouting-face.png"),
        require("../../assets/images/emoji/face-screaming-in-fear.png"),
        require("../../assets/images/emoji/face-vomiting.png"),
        require("../../assets/images/emoji/face-without-mouth.png"),
        require("../../assets/images/emoji/face-with-tongue.png"),
        require("../../assets/images/emoji/clown-face.png"),
        require("../../assets/images/emoji/new-moon-face.png"),
        require("../../assets/images/emoji/ghost.png"),
        require("../../assets/images/emoji/jack-o-lantern.png"),
        require("../../assets/images/emoji/money-bag.png"),
        require("../../assets/images/emoji/pile-of-poo.png"),
        require("../../assets/images/emoji/shamrock.png"),
        require("../../assets/images/emoji/hibiscus.png"),
        require("../../assets/images/emoji/lips.png"),
        require("../../assets/images/emoji/sparkles.png"),
        require("../../assets/images/emoji/star.png"),
        require("../../assets/images/emoji/two-hearts.png"),
        require("../../assets/images/emoji/rainbow.png"),
        require("../../assets/images/emoji/thought-balloon.png"),
      ],
    };
  },
  methods: {
    sendEmoji(item) {
      this.$emit("sendEmoji", item);
    },
    closeEmoji() {
      this.$emit("closeEmoji");
    }
  },
};
</script>

<style lang="scss" scoped>
.emoji-content {
  .emoji {
    width: 400px;
    height: 200px;
    //background-color: rgb(39, 42, 55);
    background-color: white;
    position: absolute;
    top: -220px;
    left: -10px;
    border-radius: 10px;
    transition: 0.3s;
    z-index: 3;

    &::after {
      content: "";
      display: block;
      width: 0;
      height: 0;
      //border-top: 10px solid rgb(39, 42, 55);
      border-top: 10px solid white;
      border-right: 10px solid transparent;
      border-left: 10px solid transparent;
      position: absolute;
      bottom: -8px;
      left: 15px;
      z-index: 100;
    }
    .emoji-wrapper {
      width: 100%;
      height: 100%;
      overflow-y: scroll;
      padding: 10px;
      box-sizing: border-box;
      position: relative;
      &::-webkit-scrollbar {
        /*滚动条整体样式*/
        width: 4px; /*高宽分别对应横竖滚动条的尺寸*/
        height: 1px;
      }
      &::-webkit-scrollbar-thumb {
        /*滚动条里面小方块*/
        border-radius: 10px;
        //box-shadow: inset 0 0 5px rgba(97, 184, 179, 0.1);
        box-shadow: inset 0 0 5px white;
        //background: rgb(95, 101, 122);
        background:white;
      }
      &::-webkit-scrollbar-track {
        /*滚动条里面轨道*/
        //box-shadow: inset 0 0 5px rgba(87, 175, 187, 0.1);
        box-shadow: inset 0 0 5px white;
        border-radius: 10px;
        //background: rgb(39, 42, 55);
        background: white;
      }
      .emoji-list {
        display: flex;
        justify-content: flex-start;
        flex-wrap: wrap;
        margin-left: 10px;
        .emoji-item {
          list-style: none;
          width: 50px;
          height: 50px;
          border-radius: 10px;
          margin: 5px;
          position: relative;
          cursor: pointer;
          &:hover {
            background-color: rgb(50, 54, 68);
          }
          img {
            width: 35px;
            height: 35px;
            position: absolute;
            left: 50%;
            top: 50%;
            transform: translate(-50%, -50%);
          }
        }
      }
    }

  }
  .mask {
    width: 100%;
    height: 100%;
    position: fixed;
    background: transparent;
    left: 0;
    top: 0;
    z-index: 1;
  }
}

</style>
