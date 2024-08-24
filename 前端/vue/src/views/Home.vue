<template>
  <div style="color: #666;font-size: 14px;">

    <div style="margin: 10px 0;width: 60%;">
      <el-carousel height="500px" :interval="5000">
        <el-carousel-item v-for="item in imgs" :key="item">
          <img :src="item" style="width: 100%;height: 100%;">
        </el-carousel-item>
      </el-carousel>
    </div>

    
  </div>
</template>

<script>
export default {
  name: "Home",
  data() {
    return {
      user: localStorage.getItem("user") ? JSON.parse(localStorage.getItem("user")) : {},
      imgs:["https://pic.rmb.bdstatic.com/bjh/down/ac63fc6e31c857036e11b3739d35a4ab.jpeg@wm_2,t_55m+5a625Y+3L+W5v+W3nui9r+S7tuWtpumZouaLm+WKng==,fc_ffffff,ff_U2ltSGVp,sz_27,x_17,y_17",
        "https://pic.rmb.bdstatic.com/bjh/down/4500663b7e448afd816aee53a2e007cb.png@wm_2,t_55m+5a625Y+3L+W5v+W3nui9r+S7tuWtpumZouaLm+WKng==,fc_ffffff,ff_U2ltSGVp,sz_27,x_17,y_17",
      "https://pic.rmb.bdstatic.com/bjh/down/bd9d1b7c3b741fe8a9656018e7e67950.jpeg@wm_2,t_55m+5a625Y+3L+W5v+W3nui9r+S7tuWtpumZouaLm+WKng==,fc_ffffff,ff_U2ltSGVp,sz_27,x_17,y_17",
      "https://pic.rmb.bdstatic.com/bjh/down/a17b54a3001217da6abf34e02ebca413.jpeg@wm_2,t_55m+5a625Y+3L+W5v+W3nui9r+S7tuWtpumZouaLm+WKng==,fc_ffffff,ff_U2ltSGVp,sz_27,x_17,y_17",
      ]
    }
  },
}
</script>

