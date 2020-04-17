import { Component, OnInit } from '@angular/core';
import { Submission } from 'src/app/model/submission';
import { ActivatedRoute } from '@angular/router';
import { ApiService } from 'src/app/service/api.service';

@Component({
  selector: 'app-user-score',
  templateUrl: './user-score.component.html',
  styleUrls: ['./user-score.component.css'],
})
export class UserScoreComponent implements OnInit {
  submissions: Submission[];
  userId: string;

  constructor(private route: ActivatedRoute, public apiService: ApiService) {
    this.submissions = [];
  }

  ngOnInit(): void {
    this.route.paramMap.subscribe((params) => {
      const userId: string = params.get('id');

      this.apiService
        .getValidSubmissionsByUserId(userId)
        .subscribe((submissions: Submission[]) => {
          this.userId = userId;
          this.submissions = submissions;
        });
    });
  }
}
