<template>
  <div class="workers">
    <h3>Workers</h3>
      <table id="firstTable">
        <thead>
        <tr>
          <th>ID</th>
          <th>Name</th>
          <th>Base salary</th>
          <th>Status</th>
        </tr>
        </thead>
        <tbody>
        <tr v-for="row in workers">
          <td>{{row.id}}</td>
          <td>{{row.lastName}} {{row.firstName}}</td>
          <td>{{row.baseSalary}}</td>
          <td>{{row.status}}</td>
        </tr>
        </tbody>
      </table>
    </div>
</template>

<script lang="ts">
  import {Component, Vue} from 'vue-property-decorator'
  import axios from 'axios';

  export interface Worker {
  id: number;
  firstName: string;
  lastName: string;
  baseSalary: number;
  status: string;
}

@Component({
  components: {
    //HelloWorld
  }
})

export default class Home extends Vue {
  public workers: Worker[] = [];

  private async created() {
    const response = await axios.get('/api/workers');
    this.workers = await response.data;
  }
}
</script>
<style>
  table {
    alignment: center;
    font-family: 'Open Sans', sans-serif;
    width: 750px;
    border-collapse: collapse;
    border: 1px solid rgba(170, 179, 232, 0.17);
    margin: 5px 10px 0 5px;
    text-align: left;
  }

  table th {
    background: rgba(128, 134, 174, 0.39);
    padding: 5px;
    min-width: 20px;
    border: 1px solid rgb(201, 201, 201);
  }

  table td {
    padding: 5px;
    border: 1px solid rgba(170, 179, 232, 0.17);
    min-width: 20px;
  }

</style>
