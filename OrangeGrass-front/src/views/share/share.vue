<template>
  <div class="disk-share">
    <div class="disk-share-header">
      <global-header></global-header>
    </div>
    <div class="disk-share-extractCard">
      <extract-card>
      </extract-card>
    </div>
  </div>
</template>

<script>
import ExtractCard from "./components/extractCard";
import GlobalHeader from "../header";
export default {
  name: "share",
  components: {GlobalHeader, ExtractCard}
}
</script>

<style scoped>
.disk-share-extractCard {
  margin: -240px 0 0 -230px;
  position: absolute;
  left: 50%;
  top: 50%;
}
</style>
