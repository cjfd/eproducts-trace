// 登录组件
const Login = {
 data() {
    return {
      // 用户身份
      users: [
        {
          name: '生产厂家',
          userName: 'producer',
          component: Farm,
          description:"电子产品信息注册"
        },
        {
          name: '分销商',
          userName: 'distributor',
          component: Agent,
          description:"电子产品信息记录"

        },
        {
          name: '零售商',
          userName: 'retailer',
          component: Mall,
          description:"电子产品信息记录"
        },
        {
          name: '消费者',
          userName: 'consumer',
          component: Consumer,
          description:"电子产品信息溯源"

        },
      ],
      currentUser: null, // 当前用户
      address: '', // 角色地址
      countdown: 5, // 倒计时时间
      loginItem: '', // 登录用户信息
      timer: null, // 处理倒计时的定时器
    }
  },
  template:`
  <div class="login">
        <!-- 角色选择 -->
        <h3 v-if="currentUser === null">电子产品溯源平台</h3>
        <el-row :gutter="80"  v-if="currentUser === null">
          <el-col :span="6" v-for="(item, index) in users" :key="index" >
             <el-card :body-style="{ padding: '0px'}" >
               <div class="bottom" @click="handleClick(item)">
               <span style="font-size:30px">{{ item.name }}</span>
               <br></br>
                   {{ item.description }}
               </div>
             </el-card>
          </el-col>
        </el-row>


        <!-- 角色登录 -->
        <div v-else class="is-login">
          <h3>登录中......(倒计时：{{ countdown}} 秒)</h3>
          <div>角色:
            <span>{{ currentUser }}</span>
          </div>

          <!-- 非消费者则显示角色地址 -->
          <div v-if="currentUser !== '消费者'">角色地址:
            <span>{{ address }}</span>
          </div>

          <!-- 直接登录按钮 -->
          <el-button type="primary" @click="clearTimer">直接登录</el-button>
        </div>
      </div>
  `,
methods: {
    // 登录时有个5秒的倒计时，这里是在点击直接登录时，清楚倒计时，直接跳到相关页面
    clearTimer() {
      clearInterval(this.timer);
      this.$emit('login', {
        component: this.loginItem.component,
        user: this.loginItem.name,
      });
    },
    // 倒计时
    countdownInterval({ component, name: user }) {
      this.timer = setInterval(() => {
        if(this.countdown <= 0){
          this.clearTimer();
        }
        this.countdown -= 1;
      }, 1000);
    },
   // 点击用户登录，获取用户地址
   handleClick(item) {
     this.loginItem = item;
           if (item.userName !== "consumer") {
             axios({
               method: 'get',
               url: `/userinfo?userName=${item.userName}`,
             })
             .then(ret => {
               this.address = ret.data.address;
               this.currentUser = item.name;
               this.countdownInterval(item);
             })
             .catch(err => {
               console.log(err)
             })
           } else {
             this.currentUser = item.name;
             this.countdownInterval(item);
           }
   }
  }
}
